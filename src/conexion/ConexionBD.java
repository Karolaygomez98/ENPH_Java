package conexion;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Administra la conexión con la base de datos Oracle del proyecto ENPH.
 * Cada llamada devuelve una conexión nueva; quien la use debe cerrarla
 * (idealmente con try-with-resources).
 */
public class ConexionBD {

    private static final String DRIVER = "oracle.jdbc.OracleDriver";

    // Orden de lectura: variable de entorno -> archivo config.properties -> valor por defecto.
    // La clave NO tiene valor por defecto y nunca debe escribirse en el codigo ni subirse a GitHub.
    private static final Properties CONFIG = cargarConfig();
    private static final String URL = leer("DB_URL", "jdbc:oracle:thin:@localhost:1521:xe");
    private static final String USUARIO = leer("DB_USER", "ENPH");
    private static final String CLAVE = leer("DB_PASS", "");

    private ConexionBD() { } // clase utilitaria: no se instancia

    private static Properties cargarConfig() {
        Properties p = new Properties();
        try (FileInputStream in = new FileInputStream("config.properties")) {
            p.load(in);
        } catch (IOException e) {
            // sin archivo: se usan solo las variables de entorno
        }
        return p;
    }

    private static String leer(String nombre, String porDefecto) {
        String valor = System.getenv(nombre);
        if (valor == null || valor.trim().isEmpty()) {
            valor = CONFIG.getProperty(nombre);
        }
        return (valor != null && !valor.trim().isEmpty()) ? valor.trim() : porDefecto;
    }

    /** Devuelve una conexión abierta o null si no fue posible conectar. */
    public static Connection obtenerConexion() {
        if (CLAVE.isEmpty()) {
            System.err.println("[ERROR] Falta la clave de Oracle. Cree el archivo config.properties "
                    + "(vea config.example.properties) o defina la variable de entorno DB_PASS.");
            return null;
        }
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