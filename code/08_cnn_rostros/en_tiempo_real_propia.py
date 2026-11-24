"""Clasificar una imagen recortada o rostros detectados por la webcam."""
import argparse
import json
import os
from pathlib import Path

os.environ.setdefault("TF_CPP_MIN_LOG_LEVEL", "2")

import cv2
import numpy as np
import tensorflow as tf

AQUI = Path(__file__).resolve().parent


def preparar_rostro(rostro_bgr):
    rgb = cv2.cvtColor(rostro_bgr, cv2.COLOR_BGR2RGB)
    # El modelo incluye la normalizacion: aqui los pixeles siguen en 0..255.
    return cv2.resize(rgb, (128, 128)).astype("float32")


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--imagen", type=Path, help="Rostro ya recortado; no abre la camara.")
    parser.add_argument("--camara", type=int, default=0)
    parser.add_argument("--umbral", type=float, default=0.6)
    args = parser.parse_args()
    if not 0 <= args.umbral <= 1:
        parser.error("El umbral debe estar entre 0 y 1.")
    if not (AQUI / "modelo_propia.keras").is_file():
        raise SystemExit("Falta modelo_propia.keras. Ejecuta entrenar_propia.py primero.")
    modelo = tf.keras.models.load_model(AQUI / "modelo_propia.keras", compile=False)
    clases = json.loads((AQUI / "clases_propia.json").read_text(encoding="utf-8"))
    if modelo.output_shape[-1] != len(clases):
        raise SystemExit("El modelo y las clases no corresponden. Vuelve a entrenar.")

    def predecir(rostro):
        probs = modelo(preparar_rostro(rostro)[None], training=False).numpy()[0]
        indice = int(np.argmax(probs))
        nombre = clases[indice] if probs[indice] >= args.umbral else "Desconocido"
        return nombre, float(probs[indice])

    if args.imagen:
        imagen = cv2.imread(str(args.imagen))
        if imagen is None:
            raise SystemExit(f"No se puede leer: {args.imagen}")
        nombre, confianza = predecir(imagen)
        print(json.dumps({"clase": nombre, "confianza": confianza}))
        return

    detector = cv2.CascadeClassifier(cv2.data.haarcascades + "haarcascade_frontalface_default.xml")
    if detector.empty():
        raise SystemExit("No se pudo cargar el detector de rostros.")
    camara = cv2.VideoCapture(args.camara)
    try:
        if not camara.isOpened():
            raise SystemExit("No se pudo abrir la camara.")
        while True:
            ok, cuadro = camara.read()
            if not ok:
                raise SystemExit("No se pudo leer la camara.")
            cuadro = cv2.flip(cuadro, 1)
            gris = cv2.cvtColor(cuadro, cv2.COLOR_BGR2GRAY)
            for x, y, w, h in detector.detectMultiScale(gris, 1.2, 5, minSize=(60, 60)):
                nombre, confianza = predecir(cuadro[y:y+h, x:x+w])
                cv2.rectangle(cuadro, (x, y), (x+w, y+h), (0, 200, 0), 2)
                cv2.putText(cuadro, f"{nombre} {confianza:.0%}", (x, max(20, y-8)),
                            cv2.FONT_HERSHEY_SIMPLEX, 0.6, (0, 200, 0), 2)
            cv2.imshow("CNN de rostros - Q para salir", cuadro)
            if cv2.waitKey(1) & 0xFF in (ord("q"), 27):
                break
    finally:
        camara.release()
        cv2.destroyAllWindows()


if __name__ == "__main__":
    main()
