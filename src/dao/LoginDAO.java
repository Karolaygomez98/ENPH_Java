package dao;

import conexion.ConexionBD;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/** Acceso a las columnas de ingreso (login, clave_hash, clave_sal) de la tabla usuario. */
public class LoginDAO {

    /** Datos de un usuario para validar su ingreso. */
    public static class Cuenta {
        public String idUsuario;
        public String nombre;
        public String rol;
        public String hash;   // null si todavia no creo su clave
        public String sal;
    }

    // READ
    public Cuenta buscarPorLogin(String login) {
        String sql = "SELECT id_usuario, nombre, rol, clave_hash, clave_sal FROM usuario WHERE login = ?";
        try (Connection con = ConexionBD.obtenerConexion()) {
            if (con == null) {
                return null;
            }
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, login);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        Cuenta c = new Cuenta();
                        c.idUsuario = rs.getString("id_usuario");
                        c.nombre = rs.getString("nombre");
                        c.rol = rs.getString("rol");
                        c.hash = rs.getString("clave_hash");
                        c.sal = rs.getString("clave_sal");
                        return c;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("[DAO] Error al buscar el usuario: " + e.getMessage());
        }
        return null;
    }

    // UPDATE: solo guarda la clave si el usuario todavia no tenia una
    public boolean crearClave(String login, String hash, String sal) {
        String sql = "UPDATE usuario SET clave_hash = ?, clave_sal = ? WHERE login = ? AND clave_hash IS NULL";
        try (Connection con = ConexionBD.obtenerConexion()) {
            if (con == null) {
                return false;
            }
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, hash);
                ps.setString(2, sal);
                ps.setString(3, login);
                boolean ok = ps.executeUpdate() > 0;
                if (!con.getAutoCommit()) {
                    con.commit();
                }
                return ok;
            }
        } catch (SQLException e) {
            System.err.println("[DAO] Error al guardar la clave: " + e.getMessage());
            return false;
        }
    }
}
