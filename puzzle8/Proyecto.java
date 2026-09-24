package puzzle8;
/**
 *
 * @author angel
 */
import java.util.LinkedList;
import java.util.List;

public class Proyecto {

    public static void main(String[] args) {
        Nodo nodo = new Nodo("1238 4765"); 
        ArbolDeBusqueda ab = new ArbolDeBusqueda(nodo);

 
        
        String objetivo = "1284376 5"; 


        Nodo solucion = ab.busquedaPrimeroAnchura(objetivo);
        
        if (solucion == null) {
            System.out.println("no se encontro solucion.");
            return;
        }

        System.out.println("estado de solucion: " + solucion.estado);
        System.out.println("nivel de movimientos " + solucion.getNivel());

        // imprime desde la raiz hasta la solucion
        List<String> camino = reconstruirCamino(solucion);
        System.out.println("camino (" + (camino.size() - 1) + " movimientos):");
        for (String e : camino) {
            imprimirTablero(e);
            System.out.println("-----");
        }
    }

    static List<String> reconstruirCamino(Nodo n) {
        LinkedList<String> camino = new LinkedList<>();
        while (n != null) {
            camino.addFirst(n.estado);
            n = n.padre;
        }
        return camino;
    }

    static void imprimirTablero(String e) {
        System.out.println(e.substring(0, 3));
        System.out.println(e.substring(3, 6));
        System.out.println(e.substring(6, 9));
    }
}
