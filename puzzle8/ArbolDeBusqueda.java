package puzzle8;



import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 *
 * @author angel
 */

public class ArbolDeBusqueda {
    private Nodo raiz;

    public ArbolDeBusqueda(Nodo raiz) {
        this.raiz = raiz;
    }

    public Nodo busquedaPrimeroAnchura(String estadoObjetivo) {
        if (raiz == null) return null;

        HashSet<String> visitados = new HashSet<>();
        Queue<Nodo> cola = new LinkedList<>();
        
        
        

        cola.add(raiz);
        visitados.add(raiz.estado);

        // asi le hicieron en clase
        while (!cola.isEmpty()) {
            Nodo actual = cola.poll();

            if (actual.estado.equals(estadoObjetivo)) {
                return actual;
            }

            List<Nodo> sucesores = actual.generarSucesores();
            for (Nodo sucesor : sucesores) {
                if (!visitados.contains(sucesor.estado)) {
                    visitados.add(sucesor.estado);
                    cola.add(sucesor);
                }
            }
        }
        return null;
    }
    
    
    public Nodo busquedaProfundidad(String estadoObjetivo) {
    if (raiz == null) return null;

    HashSet<String> visitados = new HashSet<>();
    Deque<Nodo> pila = new ArrayDeque<>();

    pila.push(raiz);
    visitados.add(raiz.estado);

    while (!pila.isEmpty()) {
        Nodo actual = pila.pop();

        if (actual.estado.equals(estadoObjetivo)) {
            return actual;
        }

        List<Nodo> sucesores = actual.generarSucesores();


        for (int i = sucesores.size() - 1; i >= 0; i--) {
            Nodo s = sucesores.get(i);
            if (!visitados.contains(s.estado)) {
                visitados.add(s.estado);
                pila.push(s);
            }
        }
    }
    return null;
}
 public Nodo busquedaCostoUniforme(String estadoObjetivo) {
    if (raiz == null) return null;

    HashSet<String> visitados = new HashSet<>();
    Queue<Nodo> cola = new LinkedList<>();

    cola.add(raiz);
    visitados.add(raiz.estado);

    // uno si uni no
    int[] idx = {0, 2, 4, 6, 8};

    while (!cola.isEmpty()) {
        Nodo actual = cola.poll();

        if (actual.estado.equals(estadoObjetivo)) {
            return actual;
        }

        List<Nodo> sucesores = actual.generarSucesores();

        Nodo mejor = null;
        int mejorScore = Integer.MIN_VALUE;

        // guardamos los candidatos no visitados para fallback
        List<Nodo> candidatos = new java.util.ArrayList<>();

        for (Nodo sucesor : sucesores) {
            if (visitados.contains(sucesor.estado)) continue;

            // pa reconstruir el camino
            sucesor.padre = actual;
            sucesor.nivel = actual.nivel + 1;

            // score basado en que tantos coincidieron
            int score = 0;
            for (int k : idx) {
                char c = sucesor.estado.charAt(k);
                if (c == ' ') continue;                 // no contar espacio
                if (c == estadoObjetivo.charAt(k)) score++;
            }

            sucesor.costo = score; // guardo el score en costo
            candidatos.add(sucesor);

            if (mejor == null || score > mejorScore) {
                mejor = sucesor;
                mejorScore = score;
            }
        }

        if (mejor != null) {
            // se elige el mejor
            visitados.add(mejor.estado);
            cola.add(mejor);
        } else {
            // si no hubo sucesores nuevo seguimos como el bfs esto para que no diga q no encontro solucion ps
            for (Nodo s : sucesores) {
                if (!visitados.contains(s.estado)) {
                    s.padre = actual;
                    s.nivel = actual.nivel + 1;
                    visitados.add(s.estado);
                    cola.add(s);
                }
            }
        }
    }
    return null;
}

}
