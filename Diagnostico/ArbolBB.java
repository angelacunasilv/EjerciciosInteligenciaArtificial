package arbolbinario;

import java.util.ArrayList;
import java.util.List;

public class ArbolBB {
    private static class Nodo {
        int dato;
        Nodo izquierda, derecha;
        Nodo(int dato) { this.dato = dato; }
    }

    private Nodo raiz;
    public void insertar(int dato) { raiz = insertar(raiz, dato); }

    private Nodo insertar(Nodo nodo, int dato) {
        if (nodo == null) return new Nodo(dato);
        if (dato < nodo.dato) nodo.izquierda = insertar(nodo.izquierda, dato);
        if (dato > nodo.dato) nodo.derecha = insertar(nodo.derecha, dato);
        return nodo; // No se agregan duplicados.
    }

    public boolean buscar(int dato) {
        Nodo actual = raiz;
        while (actual != null) {
            if (actual.dato == dato) return true;
            actual = dato < actual.dato ? actual.izquierda : actual.derecha;
        }
        return false;
    }

    public void eliminar(int dato) { raiz = eliminar(raiz, dato); }

    private Nodo eliminar(Nodo nodo, int dato) {
        if (nodo == null) return null;
        if (dato < nodo.dato) nodo.izquierda = eliminar(nodo.izquierda, dato);
        else if (dato > nodo.dato) nodo.derecha = eliminar(nodo.derecha, dato);
        else {
            if (nodo.izquierda == null) return nodo.derecha;
            if (nodo.derecha == null) return nodo.izquierda;
            Nodo sucesor = nodo.derecha;
            while (sucesor.izquierda != null) sucesor = sucesor.izquierda;
            nodo.dato = sucesor.dato;
            nodo.derecha = eliminar(nodo.derecha, sucesor.dato);
        }
        return nodo;
    }

    public List<Integer> inorden() { return recorrido(raiz, 0, new ArrayList<>()); }
    public List<Integer> preorden() { return recorrido(raiz, 1, new ArrayList<>()); }
    public List<Integer> postorden() { return recorrido(raiz, 2, new ArrayList<>()); }

    private List<Integer> recorrido(Nodo nodo, int orden, List<Integer> datos) {
        if (nodo == null) return datos;
        if (orden == 1) datos.add(nodo.dato);
        recorrido(nodo.izquierda, orden, datos);
        if (orden == 0) datos.add(nodo.dato);
        recorrido(nodo.derecha, orden, datos);
        if (orden == 2) datos.add(nodo.dato);
        return datos;
    }

    public static void main(String[] args) {
        ArbolBB arbol = new ArbolBB();
        for (int dato : new int[]{50, 30, 70, 20, 40, 60, 80}) arbol.insertar(dato);
        System.out.println("Inorden: " + arbol.inorden());
        System.out.println("Preorden: " + arbol.preorden());
        System.out.println("Postorden: " + arbol.postorden());
        System.out.println("Buscar 40: " + arbol.buscar(40));
        arbol.eliminar(50);
        System.out.println("Eliminar raiz 50: " + arbol.inorden());
    }
}
