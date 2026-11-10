from pathlib import Path
import tkinter as tk
from tkinter import ttk

import numpy as np
from PIL import Image, ImageDraw, ImageTk
from tensorflow.keras.models import load_model


LADO_LIENZO = 280
GROSOR = 22
LADO_MNIST = 28
LADO_DIGITO = 20
RUTA_MODELO = Path(__file__).with_name("modelo_mlp.keras")


def preparar_dibujo(imagen):
    """Convierte el lienzo en un lote de una imagen compatible con el MLP."""
    limites = imagen.getbbox()
    if limites is None:
        return None

    digito = imagen.crop(limites)
    escala = LADO_DIGITO / max(digito.size)
    ancho = max(1, round(digito.width * escala))
    alto = max(1, round(digito.height * escala))
    digito = digito.resize((ancho, alto), Image.Resampling.LANCZOS)

    # Centra el trazo según la intensidad de sus píxeles, como en MNIST.
    pixeles = np.asarray(digito, dtype=np.float32)
    centro_x = np.average(np.arange(ancho), weights=pixeles.sum(axis=0))
    centro_y = np.average(np.arange(alto), weights=pixeles.sum(axis=1))
    centro = (LADO_MNIST - 1) / 2
    x = min(max(0, round(centro - centro_x)), LADO_MNIST - ancho)
    y = min(max(0, round(centro - centro_y)), LADO_MNIST - alto)

    entrada = Image.new("L", (LADO_MNIST, LADO_MNIST))
    entrada.paste(digito, (x, y))
    return np.asarray(entrada, dtype=np.float32)[None, :, :] / 255.0


class Interfaz:
    def __init__(self, ventana, modelo):
        self.modelo = modelo
        self.imagen = Image.new("L", (LADO_LIENZO, LADO_LIENZO))
        self.trazo = ImageDraw.Draw(self.imagen)
        self.foto = ImageTk.PhotoImage(self.imagen, master=ventana)
        self.ultimo_punto = None
        self.resultado = tk.StringVar(ventana, "Dibuja un dígito del 0 al 9.")

        ventana.title("Adivina el número · MLP")
        ventana.resizable(False, False)
        panel = ttk.Frame(ventana, padding=16)
        panel.pack()
        ttk.Label(panel, text="Reconocimiento de dígitos", font=("", 16, "bold")).pack()
        ttk.Label(panel, text="Dibuja con el ratón y pulsa Predecir.").pack(pady=(4, 12))

        lienzo = tk.Canvas(
            panel, width=LADO_LIENZO, height=LADO_LIENZO, highlightthickness=0,
            cursor="crosshair",
        )
        lienzo.pack()
        lienzo.create_image(0, 0, image=self.foto, anchor="nw")
        lienzo.bind("<Button-1>", self.dibujar)
        lienzo.bind("<B1-Motion>", self.dibujar)
        lienzo.bind("<ButtonRelease-1>", self.terminar_trazo)

        botones = ttk.Frame(panel)
        botones.pack(pady=12)
        ttk.Button(botones, text="Predecir", command=self.predecir).pack(side="left", padx=4)
        ttk.Button(botones, text="Limpiar", command=self.limpiar).pack(side="left", padx=4)
        ttk.Label(panel, textvariable=self.resultado, font=("", 12)).pack()

    def dibujar(self, evento):
        punto = (evento.x, evento.y)
        if self.ultimo_punto is None:
            self.resultado.set("Pulsa Predecir cuando termines.")
        else:
            self.trazo.line((self.ultimo_punto, punto), fill=255, width=GROSOR)

        x, y = punto
        radio = GROSOR // 2
        self.trazo.ellipse((x - radio, y - radio, x + radio, y + radio), fill=255)
        self.ultimo_punto = punto
        self.foto.paste(self.imagen)

    def terminar_trazo(self, _evento):
        self.ultimo_punto = None

    def limpiar(self):
        self.imagen.paste(0, (0, 0, LADO_LIENZO, LADO_LIENZO))
        self.ultimo_punto = None
        self.foto.paste(self.imagen)
        self.resultado.set("Dibuja un dígito del 0 al 9.")

    def predecir(self):
        entrada = preparar_dibujo(self.imagen)
        if entrada is None:
            self.resultado.set("Dibuja un dígito antes de predecir.")
            return

        probabilidades = self.modelo(entrada, training=False).numpy()[0]
        digito = int(np.argmax(probabilidades))
        self.resultado.set(f"Predicción: {digito} · Confianza: {probabilidades[digito]:.1%}")


def main():
    if not RUTA_MODELO.is_file():
        raise SystemExit("Falta modelo_mlp.keras. Ejecuta MLP.py primero para entrenarlo.")

    modelo = load_model(RUTA_MODELO, compile=False)
    ventana = tk.Tk()
    Interfaz(ventana, modelo)
    ventana.mainloop()


if __name__ == "__main__":
    main()
