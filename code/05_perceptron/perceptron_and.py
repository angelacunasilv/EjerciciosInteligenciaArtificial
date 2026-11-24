"""Tarea 5: perceptron binario que aprende la compuerta AND."""
import numpy as np


def entrenar(x, y, tasa=0.1, epocas=100):
    pesos = np.zeros(x.shape[1], dtype=float)
    sesgo = 0.0
    errores_por_epoca = []
    for _ in range(epocas):
        errores = 0
        for entrada, esperado in zip(x, y):
            prediccion = int(np.dot(entrada, pesos) + sesgo >= 0)
            error = int(esperado) - prediccion
            pesos += tasa * error * entrada
            sesgo += tasa * error
            errores += int(error != 0)
        errores_por_epoca.append(errores)
        if errores == 0:
            break
    return pesos, sesgo, errores_por_epoca


def main():
    x = np.array([[0, 0], [0, 1], [1, 0], [1, 1]])
    y = np.array([0, 0, 0, 1])
    pesos, sesgo, errores = entrenar(x, y)
    resultado = (x @ pesos + sesgo >= 0).astype(int)
    print("Compuerta AND")
    print(f"Pesos: {pesos}; sesgo: {sesgo:.2f}; epocas: {len(errores)}")
    for entrada, esperado, obtenido in zip(x, y, resultado):
        print(f"{entrada.tolist()} -> esperado {esperado}, obtenido {obtenido}")
    if not np.array_equal(resultado, y):
        raise RuntimeError("El perceptron no convergio.")


if __name__ == "__main__":
    main()
