"""Tarea 8: entrenar una CNN con los rostros del equipo y cinco famosos."""
import argparse
import json
import os
from pathlib import Path

os.environ.setdefault("TF_CPP_MIN_LOG_LEVEL", "2")

import numpy as np
import tensorflow as tf
from sklearn.metrics import confusion_matrix
from sklearn.model_selection import train_test_split
from tensorflow.keras import callbacks, layers, models

AQUI = Path(__file__).resolve().parent
DATASET = AQUI.parent / "06_dataset_rostros" / "dataset"
EXTENSIONES = {".jpg", ".jpeg", ".png"}


def dividir_dataset(ruta):
    carpetas = sorted(p for p in ruta.iterdir() if p.is_dir() and not p.name.startswith("_"))
    carpetas = [p for p in carpetas if any(a.suffix.lower() in EXTENSIONES for a in p.iterdir())]
    if len(carpetas) < 2:
        raise ValueError("Se necesitan al menos dos clases con imagenes.")
    x_train, x_val, y_train, y_val = [], [], [], []
    for indice, carpeta in enumerate(carpetas):
        archivos = sorted(str(p) for p in carpeta.iterdir() if p.suffix.lower() in EXTENSIONES)
        if len(archivos) < 5:
            raise ValueError(f"Faltan imagenes en {carpeta.name}: minimo 5.")
        train, val = train_test_split(archivos, test_size=0.2, random_state=42)
        x_train.extend(train)
        x_val.extend(val)
        y_train.extend([indice] * len(train))
        y_val.extend([indice] * len(val))
    return [p.name for p in carpetas], x_train, x_val, y_train, y_val


def cargar_imagen(ruta, etiqueta):
    imagen = tf.io.decode_image(tf.io.read_file(ruta), channels=3, expand_animations=False)
    imagen.set_shape((None, None, 3))
    imagen = tf.image.resize(imagen, (128, 128))
    return imagen, etiqueta


def crear_dataset(rutas, etiquetas, lote, entrenamiento=False):
    datos = tf.data.Dataset.from_tensor_slices((rutas, etiquetas))
    if entrenamiento:
        datos = datos.shuffle(len(rutas), seed=42)
    opciones = tf.data.Options()
    opciones.threading.private_threadpool_size = 2
    return (datos.with_options(opciones).map(cargar_imagen, num_parallel_calls=2)
            .batch(lote).prefetch(1))


def crear_modelo(numero_clases):
    modelo = models.Sequential([
        layers.Input(shape=(128, 128, 3)),
        layers.Resizing(64, 64),
        layers.RandomFlip("horizontal"),
        layers.RandomRotation(0.03),
        layers.Rescaling(1.0 / 255),
        layers.Conv2D(16, 3, padding="same", activation="relu"),
        layers.MaxPooling2D(),
        layers.Conv2D(32, 3, padding="same", activation="relu"),
        layers.MaxPooling2D(),
        layers.Conv2D(64, 3, padding="same", activation="relu"),
        layers.MaxPooling2D(),
        layers.Flatten(),
        layers.Dense(64, activation="relu"),
        layers.Dropout(0.3),
        layers.Dense(numero_clases, activation="softmax"),
    ], name="cnn_rostros")
    modelo.compile(optimizer="adam", loss="sparse_categorical_crossentropy", metrics=["accuracy"])
    return modelo


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--dataset", type=Path, default=DATASET)
    parser.add_argument("--salida", type=Path, default=AQUI)
    parser.add_argument("--epocas", type=int, default=30)
    parser.add_argument("--lote", type=int, default=32)
    args = parser.parse_args()
    if args.epocas < 1 or args.lote < 1:
        parser.error("Epocas y lote deben ser positivos.")
    tf.config.threading.set_intra_op_parallelism_threads(4)
    tf.config.threading.set_inter_op_parallelism_threads(2)
    tf.keras.utils.set_random_seed(42)
    clases, x_train, x_val, y_train, y_val = dividir_dataset(args.dataset)
    train = crear_dataset(x_train, y_train, args.lote, True)
    val = crear_dataset(x_val, y_val, args.lote)
    conteos = np.bincount(y_train, minlength=len(clases))
    pesos = {i: float(len(y_train) / (len(clases) * n)) for i, n in enumerate(conteos)}
    args.salida.mkdir(parents=True, exist_ok=True)
    modelo_path = args.salida / "modelo_propia.keras"
    modelo = crear_modelo(len(clases))
    modelo.summary()
    historial = modelo.fit(train, validation_data=val, epochs=args.epocas, class_weight=pesos,
        callbacks=[
            callbacks.EarlyStopping(monitor="val_loss", patience=6, restore_best_weights=True),
            callbacks.ModelCheckpoint(modelo_path, monitor="val_loss", save_best_only=True),
            callbacks.ReduceLROnPlateau(monitor="val_loss", patience=3, factor=0.5),
        ], verbose=2)
    modelo = models.load_model(modelo_path)
    perdida, precision = modelo.evaluate(val, verbose=0)
    predicciones = modelo.predict(val, verbose=0).argmax(axis=1)
    metricas = {
        "clases": clases, "semilla": 42, "entrenamiento": len(x_train), "validacion": len(x_val),
        "epocas_ejecutadas": len(historial.history["loss"]),
        "mejor_epoca": int(np.argmin(historial.history["val_loss"]) + 1),
        "perdida_validacion": float(perdida), "precision_validacion": float(precision),
        "matriz_confusion": confusion_matrix(y_val, predicciones).tolist(),
        "nota": "Validacion por imagen; fotos de la misma sesion pueden aparecer en ambos grupos. No mide generalizacion a sesiones nuevas.",
    }
    (args.salida / "clases_propia.json").write_text(json.dumps(clases, indent=2), encoding="utf-8")

    import matplotlib
    matplotlib.use("Agg")
    import matplotlib.pyplot as plt
    fig, ejes = plt.subplots(1, 2, figsize=(10, 4))
    for eje, campo, titulo in zip(ejes, ["accuracy", "loss"], ["Exactitud", "Perdida"]):
        eje.plot(historial.history[campo], label="Entrenamiento")
        eje.plot(historial.history["val_" + campo], label="Validacion")
        eje.set(xlabel="Epoca (indice desde 0)", title=titulo)
        eje.legend()
    fig.tight_layout()
    fig.savefig(args.salida / "curvas_propia.png", dpi=130)
    plt.close(fig)
    print(json.dumps(metricas, indent=2))


if __name__ == "__main__":
    main()
