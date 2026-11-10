package dao;

import conexion.ConexionBD;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Consultas y accion de revision del supervisor (procedimiento sp_revisar_cuadernillo). */
public class RevisionDAO {

    /** Hogares con el cuadernillo cerrado (revisados o no): {id, jefe, revisado S/N}. */
    public List<String[]> disponibles() {
        List<String[]> lista = new ArrayList<>();
        String sql = "SELECT h.id_hogar, h.responsable, NVL(c.revisado, 'N') FROM hogar h JOIN cuadernillo2 c ON c.id_hogar = h.id_hogar "
                + "WHERE c.estado = 'Completado' ORDER BY h.id_hogar";
        try (Connection con = ConexionBD.obtenerConexion()) {
            if (con == null) {
                return null;
            }
            try (PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(new String[] { rs.getString(1), rs.getString(2), rs.getString(3) });
                }
            }
        } catch (SQLException e) {
            System.err.println("[DAO] Error al listar hogares para revision (existe la columna revisado?): " + e.getMessage());
            return null;
        }
        return lista;
    }

    /** Resumen del cuadernillo de un hogar para que el supervisor lo revise. Null si no existe. */
    public Map<String, String> resumen(String idHogar) {
        String sql = "SELECT h.responsable, h.direccion, c.estado, NVL(c.revisado, 'N') AS revisado, c.gastos_fiesta, "
                + "f.nombre AS forma_pago, "
                + "(SELECT COUNT(*) FROM visita v WHERE v.id_hogar = h.id_hogar) AS visitas, "
                + "(SELECT COUNT(*) FROM caracteristica_gasto_alimento k WHERE k.id_hogar = h.id_hogar) AS caracteristicas, "
                + "(SELECT COUNT(*) FROM gasto_diario g JOIN dia_recoleccion d ON d.id_dia = g.id_dia "
                + "  WHERE d.id_cuadernillo = c.id_cuadernillo) AS gastos, "
                + "fn_total_gastos_hogar(h.id_hogar) AS total_gastos, "
                + "fn_dias_con_gastos(h.id_hogar) AS dias, "
                + "(SELECT COUNT(*) FROM comida_preparada m JOIN dia_recoleccion d ON d.id_dia = m.id_dia "
                + "  WHERE d.id_cuadernillo = c.id_cuadernillo) AS comidas, "
                + "fn_total_comidas_hogar(h.id_hogar) AS total_comidas "
                + "FROM hogar h JOIN cuadernillo2 c ON c.id_hogar = h.id_hogar "
                + "LEFT JOIN forma_pago f ON f.id_forma_pago = c.id_forma_pago WHERE h.id_hogar = ?";
        try (Connection con = ConexionBD.obtenerConexion()) {
            if (con == null) {
                return null;
            }
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, idHogar);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        Map<String, String> r = new LinkedHashMap<>();
                        r.put("jefe", rs.getString("responsable"));
                        r.put("direccion", rs.getString("direccion"));
                        r.put("estado", rs.getString("estado"));
                        r.put("revisado", rs.getString("revisado"));
                        r.put("fiesta", rs.getString("gastos_fiesta"));
                        r.put("pago", rs.getString("forma_pago"));
                        r.put("visitas", rs.getString("visitas"));
                        r.put("caracteristicas", rs.getString("caracteristicas"));
                        r.put("gastos", rs.getString("gastos"));
                        r.put("totalGastos", rs.getString("total_gastos"));
                        r.put("dias", rs.getString("dias"));
                        r.put("comidas", rs.getString("comidas"));
                        r.put("totalComidas", rs.getString("total_comidas"));
                        return r;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("[DAO] Error al consultar el resumen del cuadernillo: " + e.getMessage());
        }
        return null;
    }

    /** Llama a sp_revisar_cuadernillo. Devuelve {"1" o "0", mensaje}, o null si no se pudo llamar. */
    public String[] revisar(String idHogar, String accion, String idUsuario) {
        try (Connection con = ConexionBD.obtenerConexion()) {
            if (con == null) {
                return null;
            }
            try (CallableStatement cs = con.prepareCall("{call sp_revisar_cuadernillo(?, ?, ?, ?, ?)}")) {
                cs.setString(1, idHogar);
                cs.setString(2, accion);
                cs.setString(3, idUsuario);
                cs.registerOutParameter(4, Types.INTEGER);
                cs.registerOutParameter(5, Types.VARCHAR);
                cs.execute();
                return new String[] { String.valueOf(cs.getInt(4)), cs.getString(5) };
            }
        } catch (SQLException e) {
            System.err.println("[DAO] Error en la revision (existe sp_revisar_cuadernillo?): " + e.getMessage());
            return null;
        }
    }
}
