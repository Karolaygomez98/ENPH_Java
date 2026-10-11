package dao;

import conexion.ConexionBD;
import modelos.Encuestador;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/** Acceso a datos de la tabla encuestadores (Oracle). CRUD completo. */
public class EncuestadorDAO {

    // CREATE
    public boolean registrarEncuestador(Encuestador e) {
        String sql = "INSERT INTO encuestadores (usuario, nombre_completo, rol) VALUES (?, ?, ?)";
        try (Connection con = ConexionBD.obtenerConexion()) {
            if (con == null) {
                System.err.println("[DAO] No hay conexion con Oracle: el encuestador NO se guardo.");
                return false;
            }
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, e.getUsuario());
                ps.setString(2, e.getNombreCompleto());
                ps.setString(3, e.getRol());
                boolean ok = ps.executeUpdate() > 0;
                confirmar(con);
                return ok;
            }
        } catch (SQLException ex) {
            System.err.println("[DAO] Error al insertar el encuestador: " + ex.getMessage());
            return false;
        }
    }

    // READ
    public Encuestador obtenerEncuestadorPorUsuario(String usuario) {
        String sql = "SELECT usuario, nombre_completo, rol FROM encuestadores WHERE usuario = ?";
        try (Connection con = ConexionBD.obtenerConexion()) {
            if (con == null) {
                System.err.println("[DAO] No hay conexion con Oracle: no se pudo consultar.");
                return null;
            }
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, usuario);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return mapear(rs);
                    }
                }
            }
        } catch (SQLException ex) {
            System.err.println("[DAO] Error al consultar el encuestador: " + ex.getMessage());
        }
        return null;
    }

    public List<Encuestador> obtenerTodosLosEncuestadores() {
        List<Encuestador> lista = new ArrayList<>();
        String sql = "SELECT usuario, nombre_completo, rol FROM encuestadores ORDER BY usuario";
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
        } catch (SQLException ex) {
            System.err.println("[DAO] Error al consultar los encuestadores: " + ex.getMessage());
        }
        return lista;
    }

    // UPDATE
    public boolean actualizarEncuestador(Encuestador e) {
        String sql = "UPDATE encuestadores SET nombre_completo = ?, rol = ? WHERE usuario = ?";
        try (Connection con = ConexionBD.obtenerConexion()) {
            if (con == null) {
                System.err.println("[DAO] No hay conexion con Oracle: el encuestador NO se actualizo.");
                return false;
            }
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, e.getNombreCompleto());
                ps.setString(2, e.getRol());
                ps.setString(3, e.getUsuario());
                boolean ok = ps.executeUpdate() > 0;
                confirmar(con);
                return ok;
            }
        } catch (SQLException ex) {
            System.err.println("[DAO] Error al actualizar el encuestador: " + ex.getMessage());
            return false;
        }
    }

    // DELETE
    public boolean eliminarEncuestador(String usuario) {
        String sql = "DELETE FROM encuestadores WHERE usuario = ?";
        try (Connection con = ConexionBD.obtenerConexion()) {
            if (con == null) {
                System.err.println("[DAO] No hay conexion con Oracle: el encuestador NO se elimino.");
                return false;
            }
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, usuario);
                boolean ok = ps.executeUpdate() > 0;
                confirmar(con);
                return ok;
            }
        } catch (SQLException ex) {
            System.err.println("[DAO] Error al eliminar el encuestador: " + ex.getMessage());
            return false;
        }
    }

    private Encuestador mapear(ResultSet rs) throws SQLException {
        return new Encuestador(rs.getString("usuario"), rs.getString("nombre_completo"), rs.getString("rol"));
    }

    private void confirmar(Connection con) throws SQLException {
        if (!con.getAutoCommit()) {
            con.commit();
        }
    }
}