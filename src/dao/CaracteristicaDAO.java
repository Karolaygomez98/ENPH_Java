package dao;

import conexion.ConexionBD;
import modelos.Caracteristica;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

/**
 * Acceso a la tabla caracteristica_gasto_alimento (capitulo C).
 * El id lo asigna el disparador trg_caracteristica_id (secuencia seq_caracteristica).
 */
public class CaracteristicaDAO {

    // CREATE / UPDATE: si ya existe la pareja hogar-grupo se actualiza; si no, se inserta.
    public boolean guardar(Caracteristica c) {
        String actualizar = "UPDATE caracteristica_gasto_alimento SET id_frecuencia_compra = ?, "
                + "frecuencia_habitual = (SELECT nombre FROM frecuencia_compra WHERE id_frecuencia_compra = ?), "
                + "valor_ultima_compra = ?, comprara_durante_periodo = ? "
                + "WHERE id_hogar = ? AND id_grupo_alimento = ?";
        String insertar = "INSERT INTO caracteristica_gasto_alimento (id_hogar, id_grupo_alimento, id_frecuencia_compra, "
                + "frecuencia_habitual, valor_ultima_compra, comprara_durante_periodo) "
                + "VALUES (?, ?, ?, (SELECT nombre FROM frecuencia_compra WHERE id_frecuencia_compra = ?), ?, ?)";
        try (Connection con = ConexionBD.obtenerConexion()) {
            if (con == null) {
                System.err.println("[DAO] No hay conexion con Oracle: la caracteristica NO se guardo.");
                return false;
            }
            int filas;
            try (PreparedStatement ps = con.prepareStatement(actualizar)) {
                ps.setString(1, c.getFrecuencia());
                ps.setString(2, c.getFrecuencia());
                poner(ps, 3, c.getValorUltimaCompra());
                ps.setString(4, vacioANulo(c.getComprara()));
                ps.setString(5, c.getIdHogar());
                ps.setString(6, c.getIdGrupo());
                filas = ps.executeUpdate();
            }
            if (filas == 0) {
                try (PreparedStatement ps = con.prepareStatement(insertar)) {
                    ps.setString(1, c.getIdHogar());
                    ps.setString(2, c.getIdGrupo());
                    ps.setString(3, c.getFrecuencia());
                    ps.setString(4, c.getFrecuencia());
                    poner(ps, 5, c.getValorUltimaCompra());
                    ps.setString(6, vacioANulo(c.getComprara()));
                    ps.executeUpdate();
                }
            }
            if (!con.getAutoCommit()) {
                con.commit();
            }
            return true;
        } catch (SQLException e) {
            System.err.println("[DAO] Error al guardar la caracteristica: " + e.getMessage());
            return false;
        }
    }

    // READ: respuestas del capitulo C de un hogar
    public List<Caracteristica> obtenerPorHogar(String idHogar) {
        List<Caracteristica> lista = new ArrayList<>();
        String sql = "SELECT id_hogar, id_grupo_alimento, id_frecuencia_compra, valor_ultima_compra, "
                + "comprara_durante_periodo FROM caracteristica_gasto_alimento WHERE id_hogar = ? "
                + "ORDER BY id_grupo_alimento";
        try (Connection con = ConexionBD.obtenerConexion()) {
            if (con == null) {
                System.err.println("[DAO] No hay conexion con Oracle: no se pudo consultar.");
                return lista;
            }
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, idHogar);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        double v = rs.getDouble("valor_ultima_compra");
                        Double valor = rs.wasNull() ? null : v;
                        lista.add(new Caracteristica(rs.getString("id_hogar"), rs.getString("id_grupo_alimento"),
                                rs.getString("id_frecuencia_compra"), valor, rs.getString("comprara_durante_periodo")));
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("[DAO] Error al consultar las caracteristicas: " + e.getMessage());
        }
        return lista;
    }

    // DELETE: quita la respuesta de un grupo (cuando se desmarca en la pantalla)
    public boolean eliminar(String idHogar, String idGrupo) {
        String sql = "DELETE FROM caracteristica_gasto_alimento WHERE id_hogar = ? AND id_grupo_alimento = ?";
        try (Connection con = ConexionBD.obtenerConexion()) {
            if (con == null) {
                return false;
            }
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, idHogar);
                ps.setString(2, idGrupo);
                ps.executeUpdate();
            }
            if (!con.getAutoCommit()) {
                con.commit();
            }
            return true;
        } catch (SQLException e) {
            System.err.println("[DAO] Error al eliminar la caracteristica: " + e.getMessage());
            return false;
        }
    }

    private void poner(PreparedStatement ps, int pos, Double valor) throws SQLException {
        if (valor == null) {
            ps.setNull(pos, Types.NUMERIC);
        } else {
            ps.setDouble(pos, valor);
        }
    }

    private String vacioANulo(String s) {
        return s == null || s.trim().isEmpty() ? null : s.trim();
    }
}
