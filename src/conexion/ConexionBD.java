package conexion;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Administra la conexión con la base de datos Oracle del proyecto ENPH.
 * Cada llamada devuelve una conexión nueva; quien la use debe cerrarla
 * (idealmente con try-with-resources).
 */
public class ConexionBD {

    private static final String DRIVER = "oracle.jdbc.OracleDriver";

    // Se pueden cambiar con variables de entorno sin modificar el código
    private static final String URL = leer("DB_URL", "jdbc:oracle:thin:@localhost:1521:xe");
    private static final String USUARIO = leer("DB_USER", "ENPH");
    private static final String CLAVE   = leer("DB_PASS", "enph123");

    private ConexionBD() { } // clase utilitaria: no se instancia

    private static String leer(String nombre, String porDefecto) {
        String valor = System.getenv(nombre);
        return (valor != null && !valor.isBlank()) ? valor : porDefecto;
    }

    /** Devuelve una conexión abierta o null si no fue posible conectar. */
    public static Connection obtenerConexion() {
        try {
            Class.forName(DRIVER);
            DriverManager.setLoginTimeout(5); // segundos
            return DriverManager.getConnection(URL, USUARIO, CLAVE);
        } catch (ClassNotFoundException e) {
            System.err.println("[ERROR] Driver JDBC de Oracle no encontrado. "
                    + "Revise que ojdbc14.jar esté en lib/ y en Referenced Libraries.");
        } catch (SQLException e) {
            System.err.println("[ERROR] No se pudo conectar a Oracle (" + URL + "): " + e.getMessage());
        }
        return null;
    }

    /** Abre y cierra una conexión para comprobar que todo está bien configurado. */
    public static boolean probarConexion() {
        try (Connection con = obtenerConexion()) {
            return con != null && !con.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }
}