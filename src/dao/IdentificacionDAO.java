package dao;

import conexion.ConexionBD;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.HashMap;
import java.util.Map;

/** Capitulo A (identificacion del hogar) y valor total del mercado: tablas hogar y cuadernillo2. */
public class IdentificacionDAO {

    /** Datos del hogar: id, direccion, responsable, orden, resultado, valorMercado, estado. Null si no existe o falla. */
    public Map<String, String> obtener(String idHogar) {
        String sql = "SELECT h.id_hogar, h.direccion, h.responsable, h.orden_responsable, c.resultado, "
                + "c.valor_total_mercado, c.estado, "
                + "LEAST(14, GREATEST(1, TRUNC(SYSDATE) - TRUNC(c.fecha_inicio) + 1)) FROM hogar h JOIN cuadernillo2 c ON c.id_hogar = h.id_hogar "
                + "WHERE h.id_hogar = ?";
        try (Connection con = ConexionBD.obtenerConexion()) {
            if (con == null) {
                return null;
            }
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, idHogar);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        return null;
                    }
                    Map<String, String> m = new HashMap<>();
                    m.put("id", rs.getString(1));
                    m.put("direccion", rs.getString(2));
                    m.put("responsable", rs.getString(3));
                    m.put("orden", rs.getString(4));
                    m.put("resultado", rs.getString(5));
                    m.put("valorMercado", rs.getString(6));
                    m.put("estado", rs.getString(7));
                    m.put("dias", rs.getString(8));
                    return m;
                }
            }
        } catch (SQLException e) {
            System.err.println("[DAO] Error al consultar la identificacion (ejecuto actualizacion_bloque1.sql?): " + e.getMessage());
            return null;
        }
    }

    /** Guarda el numero de orden del responsable y el resultado del cuadernillo (RF02, RF03). */
    public boolean guardarIdentificacion(String idHogar, int orden, String resultado) {
        try (Connection con = ConexionBD.obtenerConexion()) {
            if (con == null) {
                return false;
            }
            con.setAutoCommit(false);
            try (PreparedStatement p1 = con.prepareStatement("UPDATE hogar SET orden_responsable = ? WHERE id_hogar = ?");
                 PreparedStatement p2 = con.prepareStatement("UPDATE cuadernillo2 SET resultado = ? WHERE id_hogar = ?")) {
                p1.setInt(1, orden);
                p1.setString(2, idHogar);
                p2.setString(1, resultado);
                p2.setString(2, idHogar);
                int a = p1.executeUpdate();
                int b = p2.executeUpdate();
                if (a == 0 || b == 0) {
                    con.rollback();
                    return false;
                }
                con.commit();
                return true;
            } catch (SQLException e) {
                con.rollback();
                throw e;
            }
        } catch (SQLException e) {
            System.err.println("[DAO] Error al guardar la identificacion: " + e.getMessage());
            return false;
        }
    }

    /** Guarda el valor total del mercado (RF09). valor null = borrarlo. */
    public boolean guardarValorMercado(String idHogar, BigDecimal valor) {
        try (Connection con = ConexionBD.obtenerConexion()) {
            if (con == null) {
                return false;
            }
            try (PreparedStatement ps = con.prepareStatement("UPDATE cuadernillo2 SET valor_total_mercado = ? WHERE id_hogar = ?")) {
                if (valor == null) {
                    ps.setNull(1, Types.NUMERIC);
                } else {
                    ps.setBigDecimal(1, valor);
                }
                ps.setString(2, idHogar);
                return ps.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.err.println("[DAO] Error al guardar el valor del mercado: " + e.getMessage());
            return false;
        }
    }
}
