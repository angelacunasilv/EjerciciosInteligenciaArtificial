"""Tarea 4: regresion lineal con California Housing y descenso de gradiente."""
import argparse
import json
from pathlib import Path

import numpy as np
from sklearn.datasets import fetch_california_housing
from sklearn.metrics import mean_absolute_error, mean_squared_error, r2_score
from sklearn.model_selection import train_test_split


def entrenar(x, y, epocas=3000, tasa=0.05):
    media, escala = float(x.mean()), float(x.std())
    if escala == 0:
        raise ValueError("El ingreso debe tener variacion.")
    z = (x - media) / escala
    w, b = 0.0, 0.0
    costos = []
    for _ in range(epocas):
        error = w * z + b - y
        costos.append(float(np.mean(error ** 2) / 2))
        # Derivadas de J = sum(error**2) / (2*n).
        w -= tasa * float(np.mean(error * z))
        b -= tasa * float(np.mean(error))
    return w / escala, b - w * media / escala, costos


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--epocas", type=int, default=3000)
    parser.add_argument("--sin-grafica", action="store_true")
    parser.add_argument("--salida", type=Path, default=Path(__file__).parent / "resultados")
    args = parser.parse_args()
    if args.epocas < 1:
        parser.error("Las epocas deben ser positivas.")
    datos = fetch_california_housing()
    x = datos.data[:, datos.feature_names.index("MedInc")]
    y = datos.target
    x_train, x_test, y_train, y_test = train_test_split(x, y, test_size=0.2, random_state=42)
    w, b, costos = entrenar(x_train, y_train, args.epocas)
    prediccion = w * x_test + b
    exacto = np.polyfit(x_train, y_train, 1)
    resultado = {
        "dataset": "California Housing", "variable": "MedInc",
        "entrenamiento": len(x_train), "prueba": len(x_test), "semilla": 42,
        "epocas": args.epocas, "peso": w, "sesgo": b,
        "costo_inicial": costos[0], "costo_final": costos[-1],
        "mae_dolares": float(mean_absolute_error(y_test, prediccion) * 100000),
        "rmse_dolares": float(np.sqrt(mean_squared_error(y_test, prediccion)) * 100000),
        "r2_prueba": float(r2_score(y_test, prediccion)),
        "diferencia_minimos_cuadrados": float(np.max(np.abs(exacto - [w, b]))),
    }
    args.salida.mkdir(parents=True, exist_ok=True)
    (args.salida / "metricas.json").write_text(json.dumps(resultado, indent=2), encoding="utf-8")
    print(json.dumps(resultado, indent=2))
    print(f"Valor medio (USD) = 100000 * ({w:.4f} * MedInc + {b:.4f})")

    import matplotlib
    if args.sin_grafica:
        matplotlib.use("Agg")
    import matplotlib.pyplot as plt
    fig, ejes = plt.subplots(1, 2, figsize=(12, 4))
    ejes[0].scatter(x_test, y_test * 100000, s=5, alpha=0.25, label="Prueba")
    linea = np.linspace(x.min(), x.max(), 100)
    ejes[0].plot(linea, (w * linea + b) * 100000, color="red", label="Modelo")
    ejes[0].set(xlabel="Ingreso medio (decenas de miles de USD)", ylabel="Valor medio (USD)",
                title="California Housing: ingreso y valor de vivienda")
    ejes[0].legend()
    ejes[1].plot(costos)
    ejes[1].set(xlabel="Epoca", ylabel="MSE / 2", title="Costo de entrenamiento")
    fig.tight_layout()
    fig.savefig(args.salida / "regresion_california.png", dpi=130)
    if not args.sin_grafica:
        plt.show()
    plt.close(fig)


if __name__ == "__main__":
    main()
