package dao;

import conexion.ConexionBD;
import modelos.Hogar;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/** Acceso a datos de la tabla hogares (Oracle). CRUD completo. */
public class HogarDAO {

    private static final String COLUMNAS = "id_hogar, codigo_ubicacion, jefe_hogar, direccion, estado";

    // CREATE
    public boolean registrarHogar(Hogar h) {
        String sql = "INSERT INTO hogares (" + COLUMNAS + ") VALUES (?, ?, ?, ?, ?)";
        try (Connection con = ConexionBD.obtenerConexion()) {
            if (con == null) {
                System.err.println("[DAO] No hay conexion con Oracle: el hogar NO se guardo.");
                return false;
            }
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, h.getIdHogar());
                ps.setString(2, h.getCodigoUbicacion());
                ps.setString(3, h.getJefeHogar());
                ps.setString(4, h.getDireccion());
                ps.setString(5, h.getEstado());
                boolean ok = ps.executeUpdate() > 0;
                confirmar(con);
                return ok;
            }
        } catch (SQLException e) {
            System.err.println("[DAO] Error al insertar el hogar: " + e.getMessage());
            return false;
        }
    }

    // READ
    public Hogar obtenerHogarPorId(String idHogar) {
        String sql = "SELECT " + COLUMNAS + " FROM hogares WHERE id_hogar = ?";
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
        String sql = "SELECT " + COLUMNAS + " FROM hogares ORDER BY id_hogar";
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
        String sql = "UPDATE hogares SET codigo_ubicacion = ?, jefe_hogar = ?, direccion = ?, estado = ? WHERE id_hogar = ?";
        try (Connection con = ConexionBD.obtenerConexion()) {
            if (con == null) {
                System.err.println("[DAO] No hay conexion con Oracle: el hogar NO se actualizo.");
                return false;
            }
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, h.getCodigoUbicacion());
                ps.setString(2, h.getJefeHogar());
                ps.setString(3, h.getDireccion());
                ps.setString(4, h.getEstado());
                ps.setString(5, h.getIdHogar());
                boolean ok = ps.executeUpdate() > 0;
                confirmar(con);
                return ok;
            }
        } catch (SQLException e) {
            System.err.println("[DAO] Error al actualizar el hogar: " + e.getMessage());
            return false;
        }
    }

    // DELETE (los gastos del hogar se eliminan en cascada)
    public boolean eliminarHogar(String idHogar) {
        String sql = "DELETE FROM hogares WHERE id_hogar = ?";
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

    private Hogar mapear(ResultSet rs) throws SQLException {
        return new Hogar(rs.getString("id_hogar"), rs.getString("codigo_ubicacion"),
                rs.getString("jefe_hogar"), rs.getString("direccion"), rs.getString("estado"));
    }

    private void confirmar(Connection con) throws SQLException {
        if (!con.getAutoCommit()) {
            con.commit();
        }
    }
}