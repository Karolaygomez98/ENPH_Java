package dao;

import conexion.ConexionBD;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;

/** Llama al procedimiento almacenado sp_cerrar_cuadernillo (RF15 y RF16). */
public class CierreDAO {

    /** Devuelve {"1" o "0", mensaje}, o null si no se pudo llamar al procedimiento. */
    public String[] cerrar(String idHogar, String fiesta, String formaPago, String idUsuario) {
        try (Connection con = ConexionBD.obtenerConexion()) {
            if (con == null) {
                System.err.println("[DAO] No hay conexion con Oracle: no se pudo cerrar el cuadernillo.");
                return null;
            }
            try (CallableStatement cs = con.prepareCall("{call sp_cerrar_cuadernillo(?, ?, ?, ?, ?, ?)}")) {
                cs.setString(1, idHogar);
                cs.setString(2, fiesta);
                cs.setString(3, formaPago);
                cs.setString(4, idUsuario);
                cs.registerOutParameter(5, Types.INTEGER);
                cs.registerOutParameter(6, Types.VARCHAR);
                cs.execute();
                return new String[] { String.valueOf(cs.getInt(5)), cs.getString(6) };
            }
        } catch (SQLException e) {
            System.err.println("[DAO] Error al cerrar el cuadernillo (existe sp_cerrar_cuadernillo?): " + e.getMessage());
            return null;
        }
    }
}
