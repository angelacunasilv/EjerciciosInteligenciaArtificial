package puzzle8;

/**
 *
 * @author
 */
import java.util.ArrayList;
import java.util.List;



public class Nodo {
    String estado;
    Nodo padre;
    List<Nodo> hijos;  
    int nivel;
    int costo;

 
    public Nodo(String estado) {
        this.estado = estado;
        this.padre = null;
        this.nivel = 0;
        this.costo = 0;
    }

  
    public Nodo(String estado, Nodo padre) {
        this.estado = estado;
        this.padre = padre;
        this.nivel = (padre == null) ? 0 : padre.nivel + 1;
        this.costo = 0;
    }

 
    public Nodo(String estado, Nodo padre, int nivel) {
        this.estado = estado;
        this.padre = padre;
        this.nivel = nivel;
        this.costo = 0;
    }

    public String getEstado() { return estado; }
    public int getNivel() { return nivel; }
    public int getCosto() { return costo; }
    public Nodo getPadre() { return padre; }

    public List<Nodo> generarSucesores() {
        String[] estadoHijos = Puzzle8.generarSucesores(estado);
        List<Nodo> sucesores = new ArrayList<>();

        for (String estadoHijo : estadoHijos) {
            if (estadoHijo != null) {
 
                Nodo nodoHijo = new Nodo(estadoHijo, this, this.nivel + 1);
                sucesores.add(nodoHijo);
            }
        }
        return sucesores;
    }
}
