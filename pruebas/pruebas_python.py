"""Comprobaciones numericas, de particiones y del modelo guardado."""
import importlib.util
import json
from pathlib import Path
import unittest

import numpy as np

RAIZ = Path(__file__).resolve().parents[1]


def importar(nombre, ruta):
    spec = importlib.util.spec_from_file_location(nombre, RAIZ / ruta)
    modulo = importlib.util.module_from_spec(spec)
    spec.loader.exec_module(modulo)
    return modulo


class Practicas(unittest.TestCase):
    def test_regresion_recupera_recta(self):
        modulo = importar("california", "code/04_regresion_lineal/tarea-regresion-california.py")
        x = np.linspace(-10, 10, 100)
        w, b, costos = modulo.entrenar(x, 3 * x + 7, epocas=1000)
        np.testing.assert_allclose([w, b], [3, 7], atol=1e-8)
        self.assertLess(costos[-1], costos[0])

    def test_perceptron_and(self):
        modulo = importar("and_binario", "code/05_perceptron/perceptron_and.py")
        x = np.array([[0, 0], [0, 1], [1, 0], [1, 1]])
        y = np.array([0, 0, 0, 1])
        w, b, errores = modulo.entrenar(x, y)
        np.testing.assert_array_equal((x @ w + b >= 0).astype(int), y)
        self.assertEqual(errores[-1], 0)

    def test_dataset_y_particiones(self):
        modulo = importar("entrenar_rostros", "code/08_cnn_rostros/entrenar_propia.py")
        clases, train, val, y_train, y_val = modulo.dividir_dataset(RAIZ / "code/06_dataset_rostros/dataset")
        self.assertEqual(len(clases), 9)
        self.assertEqual(len(train), 3200)
        self.assertEqual(len(val), 800)
        self.assertFalse(set(train) & set(val))
        self.assertEqual(set(y_train), set(y_val))
        self.assertEqual(set(y_val), set(range(9)))

    def test_modelo_guardado_y_preprocesamiento(self):
        modulo = importar("inferir_rostros", "code/08_cnn_rostros/en_tiempo_real_propia.py")
        # BGR azul -> RGB azul; se conserva la escala porque Rescaling va en la CNN.
        azul = np.zeros((128, 128, 3), dtype=np.uint8)
        azul[:, :, 0] = 255
        imagen = modulo.preparar_rostro(azul)
        np.testing.assert_array_equal(imagen[0, 0], [0, 0, 255])
        clases = json.loads((RAIZ / "code/08_cnn_rostros/clases_propia.json").read_text())
        modelo = modulo.tf.keras.models.load_model(RAIZ / "code/08_cnn_rostros/modelo_propia.keras", compile=False)
        salida = modelo(imagen[None], training=False).numpy()[0]
        self.assertEqual(salida.shape, (len(clases),))
        self.assertTrue(np.all(np.isfinite(salida)))
        self.assertAlmostEqual(float(salida.sum()), 1.0, places=5)


if __name__ == "__main__":
    unittest.main(verbosity=2)
