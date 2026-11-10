package dao;

import conexion.ConexionBD;
import modelos.Hogar;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Acceso a datos del hogar sobre el modelo relacional: la tabla hogar guarda la
 * direccion y el responsable; el estado del hogar vive en su cuadernillo2 (Pendiente,
 * En Proceso o Completado). Al crear un hogar se crea tambien su cuadernillo.
 * CRUD completo.
 */
public class HogarDAO {

    private static final String SELECT_HOGAR =
            "SELECT h.id_hogar, h.responsable AS jefe_hogar, h.direccion, c.estado "
          + "FROM hogar h LEFT JOIN cuadernillo2 c ON c.id_hogar = h.id_hogar ";

    // CREATE
    public boolean registrarHogar(Hogar h) {
        // Al hogar se le asigna el encuestador ENC-001 si existe en la tabla usuario (si no, queda sin asignar)
        String sqlHogar = "INSERT INTO hogar (id_hogar, direccion, responsable, id_usuario) "
                + "VALUES (?, ?, ?, (SELECT MAX(id_usuario) FROM usuario WHERE id_usuario = 'ENC-001'))";
        String sqlCuadernillo = "INSERT INTO cuadernillo2 (id_cuadernillo, estado, fecha_inicio, id_hogar) "
                + "VALUES (?, ?, TRUNC(SYSDATE), ?)";
        try (Connection con = ConexionBD.obtenerConexion()) {
            if (con == null) {
                System.err.println("[DAO] No hay conexion con Oracle: el hogar NO se guardo.");
                return false;
            }
            con.setAutoCommit(false);
            try (PreparedStatement ps = con.prepareStatement(sqlHogar);
                 PreparedStatement pc = con.prepareStatement(sqlCuadernillo)) {
                ps.setString(1, h.getIdHogar());
                ps.setString(2, h.getDireccion());
                ps.setString(3, h.getJefeHogar());
                boolean ok = ps.executeUpdate() > 0;
                pc.setString(1, "CUA-" + h.getIdHogar());
                pc.setString(2, estadoCuadernillo(h.getEstado()));
                pc.setString(3, h.getIdHogar());
                ok = ok && pc.executeUpdate() > 0;
                if (ok) {
                    con.commit();
                } else {
                    con.rollback();
                }
                return ok;
            } catch (SQLException e) {
                con.rollback();
                throw e;
            }
        } catch (SQLException e) {
            System.err.println("[DAO] Error al insertar el hogar: " + e.getMessage());
            return false;
        }
    }

    // READ
    public Hogar obtenerHogarPorId(String idHogar) {
        String sql = SELECT_HOGAR + "WHERE h.id_hogar = ?";
        try (Connection con = ConexionBD.obtenerConexion()) {
            if (con == null) {
                System.err.println("[DAO] No hay conexion con Oracle: no se pudo consultar.");
                return null;
            }
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, idHogar);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return mapear(rs);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("[DAO] Error al consultar el hogar: " + e.getMessage());
        }
        return null;
    }

    public List<Hogar> obtenerTodosLosHogares() {
        List<Hogar> lista = new ArrayList<>();
        String sql = SELECT_HOGAR + "ORDER BY h.id_hogar";
        try (Connection con = ConexionBD.obtenerConexion()) {
            if (con == null) {
                System.err.println("[DAO] No hay conexion con Oracle: no se pudo consultar.");
                return lista;
            }
            try (PreparedStatement ps = con.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapear(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("[DAO] Error al consultar los hogares: " + e.getMessage());
        }
        return lista;
    }

    // UPDATE
    public boolean actualizarHogar(Hogar h) {
        String sqlHogar = "UPDATE hogar SET responsable = ?, direccion = ? WHERE id_hogar = ?";
        String sqlCuadernillo = "UPDATE cuadernillo2 SET estado = ? WHERE id_hogar = ?";
        try (Connection con = ConexionBD.obtenerConexion()) {
            if (con == null) {
                System.err.println("[DAO] No hay conexion con Oracle: el hogar NO se actualizo.");
                return false;
            }
            con.setAutoCommit(false);
            try (PreparedStatement ps = con.prepareStatement(sqlHogar);
                 PreparedStatement pc = con.prepareStatement(sqlCuadernillo)) {
                ps.setString(1, h.getJefeHogar());
                ps.setString(2, h.getDireccion());
                ps.setString(3, h.getIdHogar());
                boolean ok = ps.executeUpdate() > 0;
                pc.setString(1, estadoCuadernillo(h.getEstado()));
                pc.setString(2, h.getIdHogar());
                pc.executeUpdate();
                if (ok) {
                    con.commit();
                } else {
                    con.rollback();
                }
                return ok;
            } catch (SQLException e) {
                con.rollback();
                throw e;
            }
        } catch (SQLException e) {
            System.err.println("[DAO] Error al actualizar el hogar: " + e.getMessage());
            return false;
        }
    }

    // DELETE (el cuadernillo, los dias y los gastos del hogar se eliminan en cascada)
    public boolean eliminarHogar(String idHogar) {
        String sql = "DELETE FROM hogar WHERE id_hogar = ?";
        try (Connection con = ConexionBD.obtenerConexion()) {
            if (con == null) {
                System.err.println("[DAO] No hay conexion con Oracle: el hogar NO se elimino.");
                return false;
            }
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, idHogar);
                boolean ok = ps.executeUpdate() > 0;
                confirmar(con);
                return ok;
            }
        } catch (SQLException e) {
            System.err.println("[DAO] Error al eliminar el hogar: " + e.getMessage());
            return false;
        }
    }

    /** Convierte el texto de estado de la aplicacion a uno de los tres valores del cuadernillo2. */
    private String estadoCuadernillo(String estado) {
        String e = estado == null ? "" : estado.toLowerCase();
        if (e.contains("complet")) return "Completado";
        if (e.contains("pend")) return "Pendiente";
        return "En Proceso";
    }

    private Hogar mapear(ResultSet rs) throws SQLException {
        String id = rs.getString("id_hogar");
        String[] partes = id.split("-");
        String codigo = partes.length >= 2 ? partes[1] : "";   // ej. HOG-68001-014 -> 68001
        return new Hogar(id, codigo, rs.getString("jefe_hogar"), rs.getString("direccion"), rs.getString("estado"));
    }

    private void confirmar(Connection con) throws SQLException {
        if (!con.getAutoCommit()) {
            con.commit();
        }
    }
}
