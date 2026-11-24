package puzzle8;

import java.util.ArrayDeque;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.function.ToIntBiFunction;

public class PuzzleTree {
    private final NodoPuzzle raiz;
    public PuzzleTree(NodoPuzzle raiz) { this.raiz = raiz; }

    private boolean preparar(String objetivo) {
        Utils.validarEstado(objetivo);
        return raiz != null && Utils.esSoluble(raiz.estado, objetivo);
    }

    public NodoPuzzle busquedaPrimeroAnchura(String objetivo) { return buscar(objetivo, false); }
    public NodoPuzzle busquedaProfundidad(String objetivo) { return buscar(objetivo, true); }

    private NodoPuzzle buscar(String objetivo, boolean profundidad) {
        if (!preparar(objetivo)) return null;
        Set<String> visitados = new HashSet<>();
        Deque<NodoPuzzle> frontera = new ArrayDeque<>();
        frontera.add(new NodoPuzzle(raiz.estado));
        visitados.add(raiz.estado);
        while (!frontera.isEmpty()) {
            NodoPuzzle actual = profundidad ? frontera.removeLast() : frontera.removeFirst();
            if (actual.estado.equals(objetivo)) return actual;
            for (NodoPuzzle sucesor : actual.generarSucesores()) {
                if (visitados.add(sucesor.estado)) frontera.addLast(sucesor);
            }
        }
        return null;
    }

    public NodoPuzzle busquedaCostoUniforme(String objetivo) {
        return busquedaCostoUniforme(objetivo, (desde, hasta) -> 1);
    }

    public NodoPuzzle busquedaCostoUniforme(String objetivo,
                                      ToIntBiFunction<String, String> costoMovimiento) {
        if (!preparar(objetivo)) return null;
        PriorityQueue<NodoPuzzle> frontera = new PriorityQueue<>(
            Comparator.comparingInt(NodoPuzzle::getCosto).thenComparing(NodoPuzzle::getEstado));
        Map<String, Integer> mejorCosto = new HashMap<>();
        frontera.add(new NodoPuzzle(raiz.estado));
        mejorCosto.put(raiz.estado, 0);
        while (!frontera.isEmpty()) {
            NodoPuzzle actual = frontera.poll();
            if (actual.costo != mejorCosto.get(actual.estado)) continue;
            if (actual.estado.equals(objetivo)) return actual;
            for (NodoPuzzle sucesor : actual.generarSucesores()) {
                int paso = costoMovimiento.applyAsInt(actual.estado, sucesor.estado);
                if (paso <= 0) throw new IllegalArgumentException("El costo debe ser positivo.");
                int costo = Math.addExact(actual.costo, paso);
                if (costo < mejorCosto.getOrDefault(sucesor.estado, Integer.MAX_VALUE)) {
                    sucesor.costo = costo;
                    mejorCosto.put(sucesor.estado, costo);
                    frontera.add(sucesor);
                }
            }
        }
        return null;
    }

    public NodoPuzzle busquedaProfundidadLimitada(String objetivo, int limite) {
        if (limite < 0) throw new IllegalArgumentException("El limite no puede ser negativo.");
        if (!preparar(objetivo)) return null;
        Set<String> camino = new HashSet<>();
        camino.add(raiz.estado);
        return limitada(new NodoPuzzle(raiz.estado), objetivo, limite, camino);
    }

    private NodoPuzzle limitada(NodoPuzzle actual, String objetivo, int restante, Set<String> camino) {
        if (actual.estado.equals(objetivo)) return actual;
        if (restante == 0) return null;
        for (NodoPuzzle sucesor : actual.generarSucesores()) {
            if (!camino.add(sucesor.estado)) continue;
            NodoPuzzle solucion = limitada(sucesor, objetivo, restante - 1, camino);
            camino.remove(sucesor.estado);
            if (solucion != null) return solucion;
        }
        return null;
    }

    public NodoPuzzle busquedaIterativa(String objetivo) {
        // La mayor distancia minima entre dos tableros solubles de 8-puzzle es 31.
        return busquedaIterativa(objetivo, 31);
    }

    public NodoPuzzle busquedaIterativa(String objetivo, int limiteMaximo) {
        if (limiteMaximo < 0) throw new IllegalArgumentException("Limite invalido.");
        if (!preparar(objetivo)) return null;
        for (int limite = 0; limite <= limiteMaximo; limite++) {
            NodoPuzzle solucion = busquedaProfundidadLimitada(objetivo, limite);
            if (solucion != null) return solucion;
        }
        return null;
    }

    public NodoPuzzle busquedaBidireccional(String objetivo) {
        if (!preparar(objetivo)) return null;
        Map<String, NodoPuzzle> desdeInicio = new HashMap<>(), desdeMeta = new HashMap<>();
        Deque<NodoPuzzle> inicio = new ArrayDeque<>(), meta = new ArrayDeque<>();
        NodoPuzzle primero = new NodoPuzzle(raiz.estado), ultimo = new NodoPuzzle(objetivo);
        inicio.add(primero);
        meta.add(ultimo);
        desdeInicio.put(primero.estado, primero);
        desdeMeta.put(ultimo.estado, ultimo);
        if (primero.estado.equals(objetivo)) return primero;
        while (!inicio.isEmpty() && !meta.isEmpty()) {
            String encuentro = expandirNivel(inicio, desdeInicio, desdeMeta);
            if (encuentro == null) encuentro = expandirNivel(meta, desdeMeta, desdeInicio);
            if (encuentro != null) {
                NodoPuzzle camino = desdeInicio.get(encuentro);
                NodoPuzzle haciaMeta = desdeMeta.get(encuentro).padre;
                while (haciaMeta != null) {
                    camino = new NodoPuzzle(haciaMeta.estado, camino);
                    haciaMeta = haciaMeta.padre;
                }
                return camino;
            }
        }
        return null;
    }

    private String expandirNivel(Deque<NodoPuzzle> cola, Map<String, NodoPuzzle> propios,
                                Map<String, NodoPuzzle> contrarios) {
        int cantidad = cola.size();
        for (int i = 0; i < cantidad; i++) {
            for (NodoPuzzle sucesor : cola.removeFirst().generarSucesores()) {
                if (propios.containsKey(sucesor.estado)) continue;
                propios.put(sucesor.estado, sucesor);
                cola.addLast(sucesor);
                if (contrarios.containsKey(sucesor.estado)) return sucesor.estado;
            }
        }
        return null;
    }
}
