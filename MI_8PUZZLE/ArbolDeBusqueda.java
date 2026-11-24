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

public class ArbolDeBusqueda {
    private final Nodo raiz;
    public ArbolDeBusqueda(Nodo raiz) { this.raiz = raiz; }

    private boolean preparar(String objetivo) {
        Puzzle8.validarEstado(objetivo);
        return raiz != null && Puzzle8.esSoluble(raiz.estado, objetivo);
    }

    public Nodo busquedaPrimeroAnchura(String objetivo) { return buscar(objetivo, false); }
    public Nodo busquedaProfundidad(String objetivo) { return buscar(objetivo, true); }

    private Nodo buscar(String objetivo, boolean profundidad) {
        if (!preparar(objetivo)) return null;
        Set<String> visitados = new HashSet<>();
        Deque<Nodo> frontera = new ArrayDeque<>();
        frontera.add(new Nodo(raiz.estado));
        visitados.add(raiz.estado);
        while (!frontera.isEmpty()) {
            Nodo actual = profundidad ? frontera.removeLast() : frontera.removeFirst();
            if (actual.estado.equals(objetivo)) return actual;
            for (Nodo sucesor : actual.generarSucesores()) {
                if (visitados.add(sucesor.estado)) frontera.addLast(sucesor);
            }
        }
        return null;
    }

    public Nodo busquedaCostoUniforme(String objetivo) {
        return busquedaCostoUniforme(objetivo, (desde, hasta) -> 1);
    }

    public Nodo busquedaCostoUniforme(String objetivo,
                                      ToIntBiFunction<String, String> costoMovimiento) {
        if (!preparar(objetivo)) return null;
        PriorityQueue<Nodo> frontera = new PriorityQueue<>(
            Comparator.comparingInt(Nodo::getCosto).thenComparing(Nodo::getEstado));
        Map<String, Integer> mejorCosto = new HashMap<>();
        frontera.add(new Nodo(raiz.estado));
        mejorCosto.put(raiz.estado, 0);
        while (!frontera.isEmpty()) {
            Nodo actual = frontera.poll();
            if (actual.costo != mejorCosto.get(actual.estado)) continue;
            if (actual.estado.equals(objetivo)) return actual;
            for (Nodo sucesor : actual.generarSucesores()) {
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

    public Nodo busquedaProfundidadLimitada(String objetivo, int limite) {
        if (limite < 0) throw new IllegalArgumentException("El limite no puede ser negativo.");
        if (!preparar(objetivo)) return null;
        Set<String> camino = new HashSet<>();
        camino.add(raiz.estado);
        return limitada(new Nodo(raiz.estado), objetivo, limite, camino);
    }

    private Nodo limitada(Nodo actual, String objetivo, int restante, Set<String> camino) {
        if (actual.estado.equals(objetivo)) return actual;
        if (restante == 0) return null;
        for (Nodo sucesor : actual.generarSucesores()) {
            if (!camino.add(sucesor.estado)) continue;
            Nodo solucion = limitada(sucesor, objetivo, restante - 1, camino);
            camino.remove(sucesor.estado);
            if (solucion != null) return solucion;
        }
        return null;
    }

    public Nodo busquedaIterativa(String objetivo) {
        // La mayor distancia minima entre dos tableros solubles de 8-puzzle es 31.
        return busquedaIterativa(objetivo, 31);
    }

    public Nodo busquedaIterativa(String objetivo, int limiteMaximo) {
        if (limiteMaximo < 0) throw new IllegalArgumentException("Limite invalido.");
        if (!preparar(objetivo)) return null;
        for (int limite = 0; limite <= limiteMaximo; limite++) {
            Nodo solucion = busquedaProfundidadLimitada(objetivo, limite);
            if (solucion != null) return solucion;
        }
        return null;
    }

    public Nodo busquedaBidireccional(String objetivo) {
        if (!preparar(objetivo)) return null;
        Map<String, Nodo> desdeInicio = new HashMap<>(), desdeMeta = new HashMap<>();
        Deque<Nodo> inicio = new ArrayDeque<>(), meta = new ArrayDeque<>();
        Nodo primero = new Nodo(raiz.estado), ultimo = new Nodo(objetivo);
        inicio.add(primero);
        meta.add(ultimo);
        desdeInicio.put(primero.estado, primero);
        desdeMeta.put(ultimo.estado, ultimo);
        if (primero.estado.equals(objetivo)) return primero;
        while (!inicio.isEmpty() && !meta.isEmpty()) {
            String encuentro = expandirNivel(inicio, desdeInicio, desdeMeta);
            if (encuentro == null) encuentro = expandirNivel(meta, desdeMeta, desdeInicio);
            if (encuentro != null) {
                Nodo camino = desdeInicio.get(encuentro);
                Nodo haciaMeta = desdeMeta.get(encuentro).padre;
                while (haciaMeta != null) {
                    camino = new Nodo(haciaMeta.estado, camino);
                    haciaMeta = haciaMeta.padre;
                }
                return camino;
            }
        }
        return null;
    }

    private String expandirNivel(Deque<Nodo> cola, Map<String, Nodo> propios,
                                Map<String, Nodo> contrarios) {
        int cantidad = cola.size();
        for (int i = 0; i < cantidad; i++) {
            for (Nodo sucesor : cola.removeFirst().generarSucesores()) {
                if (propios.containsKey(sucesor.estado)) continue;
                propios.put(sucesor.estado, sucesor);
                cola.addLast(sucesor);
                if (contrarios.containsKey(sucesor.estado)) return sucesor.estado;
            }
        }
        return null;
    }
}
