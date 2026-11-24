"""Verifica imagenes, dimensiones y duplicados del dataset compartido."""
import hashlib
import json
from pathlib import Path

from PIL import Image

RUTA = Path(__file__).resolve().parent / "dataset"
EXTENSIONES = {".jpg", ".jpeg", ".png"}


def revisar(ruta=RUTA):
    clases, errores, duplicados, hashes = {}, [], [], {}
    for carpeta in sorted(p for p in ruta.iterdir() if p.is_dir() and not p.name.startswith("_")):
        archivos = sorted(p for p in carpeta.iterdir() if p.suffix.lower() in EXTENSIONES)
        if not archivos:
            continue
        clases[carpeta.name] = len(archivos)
        for archivo in archivos:
            relativo = archivo.relative_to(ruta).as_posix()
            try:
                with Image.open(archivo) as imagen:
                    imagen.load()
                    if imagen.size != (128, 128):
                        errores.append(f"{relativo}: dimensiones {imagen.size}")
                huella = hashlib.sha256(archivo.read_bytes()).hexdigest()
                if huella in hashes:
                    duplicados.append([hashes[huella], relativo])
                else:
                    hashes[huella] = relativo
            except (OSError, ValueError) as error:
                errores.append(f"{relativo}: {error}")
    return {"clases": clases, "total": sum(clases.values()),
            "errores": errores, "duplicados_exactos": duplicados}


if __name__ == "__main__":
    resultado = revisar()
    (RUTA.parent / "inventario.json").write_text(json.dumps(resultado, indent=2), encoding="utf-8")
    print(json.dumps(resultado, indent=2))
    raise SystemExit(1 if resultado["errores"] or len(resultado["clases"]) < 9 else 0)
