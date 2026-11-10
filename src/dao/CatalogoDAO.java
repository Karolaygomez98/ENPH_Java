package dao;

import conexion.ConexionBD;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * RF17: lee de Oracle los catalogos de codigos (unidad de medida, forma de adquisicion,
 * lugar de compra, frecuencia de compra y tipo de comida) para llenar las listas de la pantalla.
 * El nombre de la tabla se toma de una lista fija (nunca del usuario).
 */
public class CatalogoDAO {

    /** Devuelve los nombres del catalogo en el orden del cuadernillo; lista vacia si falla. */
    public List<String> nombres(String catalogo) {
        List<String> lista = new ArrayList<>();
        String tabla;
        String id;
        switch (catalogo) {
            case "unidad":     tabla = "unidad_medida";      id = "id_unidad_medida"; break;
            case "forma":      tabla = "forma_adquisicion";  id = "id_forma_adquisicion"; break;
            case "lugar":      tabla = "lugar_compra";       id = "id_lugar_compra"; break;
            case "frecuencia": tabla = "frecuencia_compra";  id = "id_frecuencia_compra"; break;
            case "comida":     tabla = "tipo_comida";        id = "id_tipo_comida"; break;
            default: return lista;
        }
        try (Connection con = ConexionBD.obtenerConexion()) {
            if (con == null) {
                return lista;
            }
            try (PreparedStatement ps = con.prepareStatement("SELECT " + id + ", nombre FROM " + tabla);
                 ResultSet rs = ps.executeQuery()) {
                List<String[]> filas = new ArrayList<>();
                while (rs.next()) {
                    filas.add(new String[]{rs.getString(1), rs.getString(2)});
                }
                filas.sort((a, b) -> ordenar(catalogo, a, b));
                for (String[] f : filas) {
                    lista.add(f[1]);
                }
            }
        } catch (SQLException e) {
            System.err.println("[DAO] No se pudo leer el catalogo " + catalogo + ": " + e.getMessage());
        }
        return lista;
    }

    /** Frecuencia: por su codigo numerico (1, 2, 2.1, 3...). Comida: DES, ALM, CEN, REF. Otros: por nombre, con "No especificado" y "Otra" al final. */
    private int ordenar(String catalogo, String[] a, String[] b) {
        if ("frecuencia".equals(catalogo)) {
            try {
                return Double.compare(Double.parseDouble(a[0]), Double.parseDouble(b[0]));
            } catch (NumberFormatException e) {
                return a[0].compareTo(b[0]);
            }
        }
        if ("comida".equals(catalogo)) {
            String orden = "DES,ALM,CEN,REF";
            int ia = orden.indexOf(a[0]);
            int ib = orden.indexOf(b[0]);
            return Integer.compare(ia < 0 ? 99 : ia, ib < 0 ? 99 : ib);
        }
        int ra = alFinal(a[1]) ? 1 : 0;
        int rb = alFinal(b[1]) ? 1 : 0;
        if (ra != rb) {
            return Integer.compare(ra, rb);
        }
        return a[1].compareToIgnoreCase(b[1]);
    }

    private boolean alFinal(String nombre) {
        String n = nombre == null ? "" : nombre.toLowerCase();
        return n.startsWith("no especificado") || n.equals("otra") || n.equals("otro");
    }
}
