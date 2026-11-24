package pruebas;

import arbolbinario.ArbolBB;
import puzzle8.ArbolDeBusqueda;
import puzzle8.Nodo;
import puzzle8.Puzzle8;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

public class PruebasJava {
    private static void verificar(boolean condicion, String mensaje) {
        if (!condicion) throw new AssertionError(mensaje);
    }

    private static void camino(Nodo nodo, String inicio, String meta) {
        verificar(nodo != null && nodo.getEstado().equals(meta), "No llego a la meta");
        int movimientos = nodo.getNivel();
        int pasos = 0;
        while (nodo.getPadre() != null) {
            Nodo padre = nodo.getPadre();
            verificar(Arrays.asList(Puzzle8.generarSucesores(padre.getEstado()))
                .contains(nodo.getEstado()), "Movimiento invalido");
            nodo = padre;
            pasos++;
        }
        verificar(nodo.getEstado().equals(inicio) && pasos == movimientos, "Camino incompleto");
    }

    public static void main(String[] args) {
        ArbolBB abb = new ArbolBB();
        for (int valor : new int[]{10, 2, 30, 1, 8, 20, 40, 8}) abb.insertar(valor);
        verificar(abb.inorden().equals(Arrays.asList(1, 2, 8, 10, 20, 30, 40)), "Orden numerico");
        abb.eliminar(10); // Raiz con dos hijos.
        abb.eliminar(1); // Hoja.
        abb.eliminar(2); // Nodo con un hijo.
        abb.eliminar(999);
        verificar(abb.inorden().equals(Arrays.asList(8, 20, 30, 40)), "Borrado incorrecto");
        verificar(abb.buscar(8) && !abb.buscar(10), "Busqueda incorrecta");
        for (int valor : new int[]{8, 20, 30, 40}) abb.eliminar(valor);
        verificar(abb.inorden().isEmpty(), "El arbol deberia quedar vacio");

        String meta = "12345678 ";
        Queue<String> cola = new ArrayDeque<>();
        Map<String, Integer> distancias = new HashMap<>();
        cola.add(meta);
        distancias.put(meta, 0);
        int casos = 0;
        while (!cola.isEmpty()) {
            String inicio = cola.remove();
            int distancia = distancias.get(inicio);
            ArbolDeBusqueda arbol = new ArbolDeBusqueda(new Nodo(inicio));
            Nodo[] resultados = {arbol.busquedaPrimeroAnchura(meta), arbol.busquedaCostoUniforme(meta),
                arbol.busquedaIterativa(meta, distancia), arbol.busquedaBidireccional(meta)};
            for (Nodo resultado : resultados) {
                camino(resultado, inicio, meta);
                verificar(resultado.getNivel() == distancia, "No encontro camino optimo");
                verificar(resultado.getCosto() == distancia, "Costo acumulado incorrecto");
            }
            if (distancia > 0) verificar(arbol.busquedaProfundidadLimitada(meta, distancia - 1) == null,
                "La limitada supero el limite");
            casos++;
            if (distancia < 6) {
                for (String hijo : Puzzle8.generarSucesores(inicio)) {
                    Puzzle8.validarEstado(hijo);
                    verificar(Arrays.asList(Puzzle8.generarSucesores(hijo)).contains(inicio),
                        "Movimiento no reversible");
                    if (distancias.putIfAbsent(hijo, distancia + 1) == null) cola.add(hijo);
                }
            }
        }
        ArbolDeBusqueda arbol = new ArbolDeBusqueda(new Nodo(meta));
        Nodo ponderado = arbol.busquedaCostoUniforme("1234567 8",
            (desde, hasta) -> desde.equals(meta) && hasta.equals("1234567 8") ? 100 : 1);
        camino(ponderado, meta, "1234567 8");
        verificar(ponderado.getNivel() > 1 && ponderado.getCosto() < 100,
            "UCS eligio el camino corto caro en vez del barato");
        verificar(arbol.busquedaIterativa("12345687 ") == null, "Acepto tablero insoluble");
        verificar(arbol.busquedaPrimeroAnchura("12345687 ") == null, "BFS insoluble");
        verificar(arbol.busquedaProfundidad("12345687 ") == null, "DFS insoluble");
        try {
            new Nodo("12345677 ");
            throw new AssertionError("Acepto fichas repetidas");
        } catch (IllegalArgumentException esperado) { }
        System.out.println("ABB y " + casos + " tableros verificados con BFS/UCS/IDS/bidireccional.");
        System.out.println("UCS ponderado: " + ponderado.getNivel() + " movimientos, costo " + ponderado.getCosto());
    }
}
