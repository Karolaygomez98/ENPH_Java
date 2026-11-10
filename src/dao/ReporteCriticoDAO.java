package dao;

import conexion.ConexionBD;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/** Acceso a la tabla reporte_critico: inconsistencias que el Critico-Digitador reporta de un hogar. */
public class ReporteCriticoDAO {

    /**
     * Guarda las inconsistencias {regla, detalle} como reportes del usuario. No repite un reporte igual
     * (mismo hogar, regla y detalle) hecho el mismo dia. Devuelve cuantos se guardaron, o -1 si fallo.
     */
    public int guardar(String idHogar, String idUsuario, List<String[]> hallazgos) {
        String sql = "INSERT INTO reporte_critico (id_hogar, id_usuario, regla, detalle) "
                + "SELECT ?, ?, ?, ? FROM dual WHERE NOT EXISTS (SELECT 1 FROM reporte_critico "
                + "WHERE id_hogar = ? AND regla = ? AND detalle = ? AND TRUNC(fecha_reporte) = TRUNC(SYSDATE))";
        try (Connection con = ConexionBD.obtenerConexion()) {
            if (con == null) {
                System.err.println("[DAO] No hay conexion con Oracle: el reporte NO se guardo.");
                return -1;
            }
            con.setAutoCommit(false);
            int guardados = 0;
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                for (String[] h : hallazgos) {
                    ps.setString(1, idHogar);
                    ps.setString(2, idUsuario);
                    ps.setString(3, h[0]);
                    ps.setString(4, h[1]);
                    ps.setString(5, idHogar);
                    ps.setString(6, h[0]);
                    ps.setString(7, h[1]);
                    guardados += ps.executeUpdate();
                }
                con.commit();
            } catch (SQLException e) {
                con.rollback();
                throw e;
            }
            return guardados;
        } catch (SQLException e) {
            System.err.println("[DAO] Error al guardar el reporte (existe la tabla reporte_critico?): " + e.getMessage());
            return -1;
        }
    }

    /** Reportes del hogar, del mas reciente al mas antiguo: {fecha, reportado por, regla, detalle}. Null si fallo. */
    public List<String[]> listar(String idHogar) {
        String sql = "SELECT TO_CHAR(r.fecha_reporte, 'DD/MM/YYYY HH24:MI'), u.nombre, r.regla, r.detalle "
                + "FROM reporte_critico r JOIN usuario u ON u.id_usuario = r.id_usuario "
                + "WHERE r.id_hogar = ? ORDER BY r.fecha_reporte DESC, r.id_reporte DESC";
        List<String[]> lista = new ArrayList<>();
        try (Connection con = ConexionBD.obtenerConexion()) {
            if (con == null) {
                return null;
            }
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, idHogar);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        lista.add(new String[] { rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4) });
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("[DAO] Error al listar reportes (existe la tabla reporte_critico?): " + e.getMessage());
            return null;
        }
        return lista;
    }
}
