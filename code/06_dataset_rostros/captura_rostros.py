"""Agregar capturas de una persona al dataset (se ejecuta manualmente)."""
# Dataset compartido del equipo:
# https://github.com/majisus7245-sudo/Curso_Inteligencia_Artificial/tree/e77bc875b74efa3a3410461199210bc413040837/code/06_dataset_rostros/dataset
import argparse
from pathlib import Path
import re

import cv2


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("nombre", help="Etiqueta de la persona, sin espacios.")
    parser.add_argument("--camara", type=int, default=0)
    args = parser.parse_args()
    if not re.fullmatch(r"[A-Za-z0-9_]+", args.nombre):
        parser.error("Usa solo letras sin acentos, numeros y guion bajo.")
    destino = Path(__file__).resolve().parent / "dataset" / args.nombre
    destino.mkdir(exist_ok=True)
    detector = cv2.CascadeClassifier(cv2.data.haarcascades + "haarcascade_frontalface_default.xml")
    if detector.empty():
        raise SystemExit("No se encontro el detector de rostros.")
    camara = cv2.VideoCapture(args.camara)
    numero = 1
    print("S: guardar rostro si se detecta solo uno. Q: salir.")
    try:
        if not camara.isOpened():
            raise SystemExit("No se pudo abrir la camara.")
        while True:
            ok, cuadro = camara.read()
            if not ok:
                raise SystemExit("No se pudo leer la camara.")
            cuadro = cv2.flip(cuadro, 1)
            gris = cv2.cvtColor(cuadro, cv2.COLOR_BGR2GRAY)
            caras = detector.detectMultiScale(gris, 1.2, 5, minSize=(80, 80))
            vista = cuadro.copy()
            for x, y, w, h in caras:
                cv2.rectangle(vista, (x, y), (x+w, y+h), (0, 200, 0), 2)
            cv2.imshow("Captura - S guardar, Q salir", vista)
            tecla = cv2.waitKey(1) & 0xFF
            if tecla in (ord("q"), 27):
                break
            if tecla == ord("s") and len(caras) == 1:
                x, y, w, h = caras[0]
                while (destino / f"captura_{numero:04d}.jpg").exists():
                    numero += 1
                rostro = cv2.resize(cuadro[y:y+h, x:x+w], (128, 128))
                archivo = destino / f"captura_{numero:04d}.jpg"
                if not cv2.imwrite(str(archivo), rostro):
                    raise OSError(f"No se pudo guardar {archivo}")
                print(archivo.name)
                numero += 1
    finally:
        camara.release()
        cv2.destroyAllWindows()


if __name__ == "__main__":
    main()
