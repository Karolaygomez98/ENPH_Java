package dao;

import conexion.ConexionBD;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/** Datos de apoyo para cargar la interfaz desde Oracle. */
public class DatosDAO {

    /** Fecha de inicio (dd/mm/aaaa) del periodo de 14 dias de cada hogar. */
    public Map<String, String> fechasInicio() {
        Map<String, String> mapa = new HashMap<>();
        String sql = "SELECT id_hogar, TO_CHAR(fecha_inicio, 'DD/MM/YYYY') FROM cuadernillo2";
        try (Connection con = ConexionBD.obtenerConexion()) {
            if (con == null) {
                return mapa;
            }
            try (PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    mapa.put(rs.getString(1), rs.getString(2));
                }
            }
        } catch (SQLException e) {
            System.err.println("[DAO] Error al consultar las fechas de inicio: " + e.getMessage());
        }
        return mapa;
    }
}
