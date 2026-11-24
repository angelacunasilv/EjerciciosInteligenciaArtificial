package puzzle8;

import java.util.LinkedList;
import java.util.List;

public class App {
    public static void main(String[] args) {
        String algoritmo = args.length > 0 ? args[0] : "bfs";
        String inicio = args.length > 1 ? args[1] : "1238 4765";
        String objetivo = args.length > 2 ? args[2] : "1284376 5";
        int limite = args.length > 3 ? Integer.parseInt(args[3]) : 31;
        PuzzleTree arbol = new PuzzleTree(new NodoPuzzle(inicio));
        String[] algoritmos = algoritmo.equals("comparar")
            ? new String[]{"bfs", "dfs", "ucs", "limitada", "iterativa", "bidireccional"}
            : new String[]{algoritmo};
        for (String nombre : algoritmos) {
            long tiempo = System.nanoTime();
            NodoPuzzle solucion;
            switch (nombre) {
                case "bfs": solucion = arbol.busquedaPrimeroAnchura(objetivo); break;
                case "dfs": solucion = arbol.busquedaProfundidad(objetivo); break;
                case "ucs": solucion = arbol.busquedaCostoUniforme(objetivo); break;
                case "limitada": solucion = arbol.busquedaProfundidadLimitada(objetivo, limite); break;
                case "iterativa": solucion = arbol.busquedaIterativa(objetivo, limite); break;
                case "bidireccional": solucion = arbol.busquedaBidireccional(objetivo); break;
                default: throw new IllegalArgumentException("Algoritmo desconocido: " + nombre);
            }
            double segundos = (System.nanoTime() - tiempo) / 1_000_000_000.0;
            if (solucion == null) {
                System.out.println(nombre + ": sin solucion dentro del limite o tablero insoluble.");
                continue;
            }
            System.out.printf("%s: movimientos=%d, costo=%d, segundos=%.6f%n",
                nombre, solucion.nivel, solucion.costo, segundos);
            if (!algoritmo.equals("comparar")) {
                for (String estado : reconstruirCamino(solucion)) {
                    imprimirTablero(estado);
                    System.out.println("-----");
                }
            }
        }
    }

    static List<String> reconstruirCamino(NodoPuzzle nodo) {
        LinkedList<String> camino = new LinkedList<>();
        while (nodo != null) {
            camino.addFirst(nodo.estado);
            nodo = nodo.padre;
        }
        return camino;
    }

    static void imprimirTablero(String estado) {
        for (int fila = 0; fila < 9; fila += 3) System.out.println(estado.substring(fila, fila + 3));
    }
}
