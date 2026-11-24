package puzzle8;

/**
 *
 * @author
 */
import java.util.ArrayList;
import java.util.List;



public class NodoPuzzle {
    String estado;
    NodoPuzzle padre;
    List<NodoPuzzle> hijos;
    int nivel;
    int costo;


    public NodoPuzzle(String estado) {
        Utils.validarEstado(estado);
        this.estado = estado;
        this.padre = null;
        this.nivel = 0;
        this.costo = 0;
    }


    public NodoPuzzle(String estado, NodoPuzzle padre) {
        Utils.validarEstado(estado);
        this.estado = estado;
        this.padre = padre;
        this.nivel = (padre == null) ? 0 : padre.nivel + 1;
        this.costo = padre == null ? 0 : padre.costo + 1;
    }


    public NodoPuzzle(String estado, NodoPuzzle padre, int nivel) {
        Utils.validarEstado(estado);
        this.estado = estado;
        this.padre = padre;
        this.nivel = nivel;
        this.costo = padre == null ? 0 : padre.costo + 1;
    }

    public String getEstado() { return estado; }
    public int getNivel() { return nivel; }
    public int getCosto() { return costo; }
    public NodoPuzzle getPadre() { return padre; }

    public List<NodoPuzzle> generarSucesores() {
        String[] estadoHijos = Utils.generarSucesores(estado);
        List<NodoPuzzle> sucesores = new ArrayList<>();

        for (String estadoHijo : estadoHijos) {
            if (estadoHijo != null) {

                NodoPuzzle nodoHijo = new NodoPuzzle(estadoHijo, this, this.nivel + 1);
                sucesores.add(nodoHijo);
            }
        }
        return sucesores;
    }
}
