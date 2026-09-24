package puzzle8;
/**
 *
 * @author angel
 */
public class Puzzle8 {
    // private String estadoInicial = "1238 4765";
    // private String estadoObjetivo = "12 843765";

    public static String[] generarSucesores(String estadoActual) {

        String[] sucesores = new String[4];
        int indice = estadoActual.indexOf(" ");

        switch (indice) {

            // posicion 0
            case 0: 
                sucesores = new String[2];

                // mueve de la derecha
                sucesores[0] = estadoActual.charAt(1) + " " + estadoActual.substring(2);

                // mueve de abajo
                sucesores[1] = estadoActual.charAt(3) + estadoActual.substring(1, 3)
                        + " " + estadoActual.substring(4);

                break;

            // posicion 1
            case 1: // genera 3 sucesores
                sucesores = new String[3];

                // mueve de la izquierda
                sucesores[0] = " " + estadoActual.charAt(0) + estadoActual.substring(2);

                // mueve de la derecha
                sucesores[1] = estadoActual.substring(0, 1) + estadoActual.charAt(2)
                        + " " + estadoActual.substring(3);

                // mueve de abajo
                sucesores[2] = estadoActual.substring(0, 1) + estadoActual.charAt(4)
                        + estadoActual.substring(2, 4) + " " + estadoActual.substring(5);

                break;

            // posicion 2
            case 2: // genera 2 sucesores
                sucesores = new String[2];

                // mueve de la izquiera
                sucesores[0] = estadoActual.substring(0, 1) + " "
                        + estadoActual.charAt(1) + estadoActual.substring(3);

                // mueve de abajo
                sucesores[1] = estadoActual.substring(0, 2) + estadoActual.charAt(5)
                        + estadoActual.substring(3, 5) + " " + estadoActual.substring(6);

                break;

            // posicion 3
            case 3: // genera 3 sucesores
                sucesores = new String[3];

                // mueve de arriba
                sucesores[0] = " " + estadoActual.substring(1, 3)
                        + estadoActual.charAt(0) + estadoActual.substring(4);

                // mueve de la derecha
                sucesores[1] = estadoActual.substring(0, 3) + estadoActual.charAt(4)
                        + " " + estadoActual.substring(5);

                // mueve de abajo
                sucesores[2] = estadoActual.substring(0, 3) + estadoActual.charAt(6)
                        + estadoActual.substring(4, 6) + " " + estadoActual.substring(7);

                break;

            // posicion 4
            case 4: // genera 4 sucesores
                sucesores = new String[4];

                // mueve de arriva
                sucesores[0] = estadoActual.substring(0, 1) + " "
                        + estadoActual.substring(2, 4) + estadoActual.charAt(1)
                        + estadoActual.substring(5);

                // mueve de abajo
                sucesores[1] = estadoActual.substring(0, 4) + estadoActual.charAt(7)
                        + estadoActual.substring(5, 7) + " " + estadoActual.substring(8);

                // mueve de izquiera
                sucesores[2] = estadoActual.substring(0, 3) + " "
                        + estadoActual.charAt(3) + estadoActual.substring(5);

                // mueve de derecha
                sucesores[3] = estadoActual.substring(0, 4) + estadoActual.charAt(5)
                        + " " + estadoActual.substring(6);

                break;

            // posicion 5
            case 5: // genera 3 sucesores
                sucesores = new String[3];

                // mueve de arriba
                sucesores[0] = estadoActual.substring(0, 2) + " "
                        + estadoActual.substring(3, 5) + estadoActual.charAt(2)
                        + estadoActual.substring(6);

                // mueve de izquierda
                sucesores[1] = estadoActual.substring(0, 4) + " "
                        + estadoActual.charAt(4) + estadoActual.substring(6);

                // mueve de abajo
                sucesores[2] = estadoActual.substring(0, 5) + estadoActual.charAt(8)
                        + estadoActual.substring(6, 8) + " ";

                break;
                // posicion 6
            case 6: // genera 2 sucesores
                sucesores = new String[2];

                // mueve de arriva
                sucesores[0] = estadoActual.substring(0, 3) + " "
                        + estadoActual.substring(4, 6) + estadoActual.charAt(3)
                        + estadoActual.substring(7);

                // mueve de derecha
                sucesores[1] = estadoActual.substring(0, 6) + estadoActual.charAt(7)
                        + " " + estadoActual.charAt(8);

                break;

            // posicion 7
            case 7: // genera 3 sucesores
                sucesores = new String[3];

                // mueve de arriba
                sucesores[0] = estadoActual.substring(0, 4) + " "
                        + estadoActual.substring(5, 7) + estadoActual.charAt(4)
                        + estadoActual.substring(8);

                // mueve de izquierda
                sucesores[1] = estadoActual.substring(0, 6) + " "
                        + estadoActual.charAt(6) + estadoActual.substring(8);

                // mueve de derecha
                sucesores[2] = estadoActual.substring(0, 7) + estadoActual.charAt(8)
                        + " ";

                break;

            // posicion 8
            case 8: // genera 2 sucesores
                sucesores = new String[2];

                // mueve de arriba
                sucesores[0] = estadoActual.substring(0, 5) + " "
                        + estadoActual.substring(6, 8) + estadoActual.charAt(5);

                // mueve de izquierda
                sucesores[1] = estadoActual.substring(0, 7) + " "
                        + estadoActual.charAt(7);

                break;
        }

        return sucesores;
    }
}