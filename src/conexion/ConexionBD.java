package conexion;

import java.sql.Connection;
import java.sql.DriverManager;

public class ConexionBD {
    // Parámetros de conexión para Oracle SQL (Estándar UDI)
    private static final String URL = "jdbc:oracle:thin:@localhost:1521:xe";
    private static final String USUARIO = "system";
    private static final String CLAVE = "oracle";

    public static Connection obtenerConexion() {
        try {
            // Carga del driver JDBC de Oracle
            Class.forName("oracle.jdbc.OracleDriver");
            return DriverManager.getConnection(URL, USUARIO, CLAVE);
        } catch (ClassNotFoundException e) {
            System.out.println("⚠️ [AVISO] Driver JDBC de Oracle no cargado en el Classpath.");
            return null;
        } catch (Exception e) {
            System.out.println("⚠️ [AVISO] No se pudo conectar a la base de datos Oracle en localhost:1521.");
            return null;
        }
    }
}