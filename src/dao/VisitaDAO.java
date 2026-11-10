package dao;

import conexion.ConexionBD;
import modelos.Visita;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/** Acceso a la tabla visita (capitulo B). Todas las consultas usan PreparedStatement. */
public class VisitaDAO {

    private static final String SELECT_VISITA =
            "SELECT id_visita, id_hogar, TO_CHAR(fecha,'DD/MM/YYYY') AS fecha, hora_inicio, hora_fin, "
          + "resultado, partes_pendientes, TO_CHAR(fecha_proxima,'DD/MM/YYYY') AS fecha_proxima, hora_proxima "
          + "FROM visita ";

    // CREATE: si ya existe la misma visita (hogar, fecha y horas) devuelve la que hay, sin duplicar.
    public boolean registrarVisita(Visita v) {
        String existente = SELECT_VISITA
                + "WHERE id_hogar = ? AND fecha = TO_DATE(?, 'DD/MM/YYYY') AND hora_inicio = ? AND hora_fin = ?";
        String insertar = "INSERT INTO visita (id_visita, fecha, hora_inicio, hora_fin, resultado, partes_pendientes, "
                + "fecha_proxima, hora_proxima, id_hogar) VALUES (?, TO_DATE(?, 'DD/MM/YYYY'), ?, ?, ?, ?, "
                + "TO_DATE(?, 'DD/MM/YYYY'), ?, ?)";
        try (Connection con = ConexionBD.obtenerConexion()) {
            if (con == null) {
                System.err.println("[DAO] No hay conexion con Oracle: la visita NO se guardo.");
                return false;
            }
            try (PreparedStatement ps = con.prepareStatement(existente)) {
                ps.setString(1, v.getIdHogar());
                ps.setString(2, v.getFecha());
                ps.setString(3, v.getHoraInicio());
                ps.setString(4, v.getHoraFin());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        v.setIdVisita(rs.getString("id_visita"));
                        return true;
                    }
                }
            }
            long id;
            try (PreparedStatement ps = con.prepareStatement("SELECT seq_visita.NEXTVAL FROM dual");
                 ResultSet rs = ps.executeQuery()) {
                rs.next();
                id = rs.getLong(1);
            }
            try (PreparedStatement ps = con.prepareStatement(insertar)) {
                ps.setLong(1, id);
                ps.setString(2, v.getFecha());
                ps.setString(3, v.getHoraInicio());
                ps.setString(4, v.getHoraFin());
                ps.setString(5, v.getResultado());
                ps.setString(6, vacioANulo(v.getPartesPendientes()));
                ps.setString(7, vacioANulo(v.getFechaProxima()));
                ps.setString(8, vacioANulo(v.getHoraProxima()));
                ps.setString(9, v.getIdHogar());
                ps.executeUpdate();
            }
            if (!con.getAutoCommit()) {
                con.commit();
            }
            v.setIdVisita(String.valueOf(id));
            return true;
        } catch (SQLException e) {
            System.err.println("[DAO] Error al registrar la visita: " + e.getMessage());
            return false;
        }
    }

    // READ: visitas de un hogar, de la primera a la ultima
    public List<Visita> obtenerVisitasPorHogar(String idHogar) {
        List<Visita> lista = new ArrayList<>();
        String sql = SELECT_VISITA + "WHERE id_hogar = ? ORDER BY fecha, hora_inicio, id_visita";
        try (Connection con = ConexionBD.obtenerConexion()) {
            if (con == null) {
                System.err.println("[DAO] No hay conexion con Oracle: no se pudieron consultar las visitas.");
                return lista;
            }
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, idHogar);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        lista.add(new Visita(rs.getString("id_visita"), rs.getString("id_hogar"),
                                rs.getString("fecha"), rs.getString("hora_inicio"), rs.getString("hora_fin"),
                                rs.getString("resultado"), rs.getString("partes_pendientes"),
                                rs.getString("fecha_proxima"), rs.getString("hora_proxima")));
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("[DAO] Error al consultar las visitas: " + e.getMessage());
        }
        return lista;
    }

    private String vacioANulo(String s) {
        return s == null || s.trim().isEmpty() ? null : s.trim();
    }
}
