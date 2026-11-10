package dao;

import conexion.ConexionBD;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/** Llama al procedimiento almacenado sp_validar_hogar (RF21) y devuelve las inconsistencias. */
public class ValidacionDAO {

    /** Codigo JDBC del tipo CURSOR de Oracle (oracle.jdbc.OracleTypes.CURSOR). */
    private static final int ORACLE_CURSOR = -10;

    /** Devuelve filas {regla, detalle}; lista vacia = hogar consistente. Null si fallo la llamada. */
    public List<String[]> validarHogar(String idHogar) {
        List<String[]> hallazgos = new ArrayList<>();
        try (Connection con = ConexionBD.obtenerConexion()) {
            if (con == null) {
                System.err.println("[DAO] No hay conexion con Oracle: no se pudo validar.");
                return null;
            }
            try (CallableStatement cs = con.prepareCall("{call sp_validar_hogar(?, ?)}")) {
                cs.setString(1, idHogar);
                cs.registerOutParameter(2, ORACLE_CURSOR);
                cs.execute();
                try (ResultSet rs = (ResultSet) cs.getObject(2)) {
                    while (rs.next()) {
                        hallazgos.add(new String[] { rs.getString(1), rs.getString(2) });
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("[DAO] Error al validar el hogar (existe sp_validar_hogar?): " + e.getMessage());
            return null;
        }
        return hallazgos;
    }
}
