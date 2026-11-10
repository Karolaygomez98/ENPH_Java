package dao;

import conexion.ConexionBD;
import modelos.ComidaPreparada;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/** Acceso a la tabla comida_preparada (capitulo D1). Usa PreparedStatement en todas las consultas. */
public class ComidaPreparadaDAO {

    private final GastoDiarioDAO gastoDAO = new GastoDiarioDAO();   // reutiliza obtenerOCrearDia

    /** Convierte el texto de la pantalla en el codigo del catalogo tipo_comida. */
    static String codigoTipo(String tipo) {
        String t = tipo == null ? "" : tipo.toLowerCase();
        if (t.startsWith("desayuno")) return "DES";
        if (t.startsWith("almuerzo")) return "ALM";
        if (t.contains("cena") || t.startsWith("comida")) return "CEN";
        return "REF";
    }

    // CREATE
    public boolean registrarComida(ComidaPreparada c) {
        String sql = "INSERT INTO comida_preparada (id_comida, descripcion, valor_pagado, cantidad, domicilio, "
                + "id_dia, id_tipo_comida, id_lugar_compra, id_tipo_gasto, id_forma_adquisicion, id_frecuencia_compra) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = ConexionBD.obtenerConexion()) {
            if (con == null) {
                System.err.println("[DAO] No hay conexion con Oracle: la comida NO se guardo.");
                return false;
            }
            con.setAutoCommit(false);
            try {
                long idDia = gastoDAO.obtenerOCrearDia(con, c.getIdHogar(), c.getDia());
                if (idDia < 0) {
                    con.rollback();
                    return false;
                }
                long id;
                try (PreparedStatement ps = con.prepareStatement("SELECT seq_comida_preparada.NEXTVAL FROM dual");
                     ResultSet rs = ps.executeQuery()) {
                    rs.next();
                    id = rs.getLong(1);
                }
                String idLugar = gastoDAO.buscarOCrearCatalogo(con, "lugar_compra", "id_lugar_compra",
                        c.getLugarCompra(), "No especificado", "L");
                String idForma = gastoDAO.buscarOCrearCatalogo(con, "forma_adquisicion", "id_forma_adquisicion",
                        c.getFormaAdquisicion(), "Comprado", "F");
                String idFrecuencia = gastoDAO.idFrecuencia(con, c.getFrecuenciaCompra());
                try (PreparedStatement ps = con.prepareStatement(sql)) {
                    ps.setLong(1, id);
                    ps.setString(2, c.getEstablecimiento());
                    ps.setDouble(3, c.getValor());
                    ps.setInt(4, c.getCantidad());
                    ps.setString(5, c.isDomicilio() ? "S" : "N");
                    ps.setLong(6, idDia);
                    ps.setString(7, codigoTipo(c.getTipo()));
                    ps.setString(8, idLugar);
                    ps.setString(9, gastoDAO.idTipoGasto(c.getCategoria()));
                    ps.setString(10, idForma);
                    ps.setString(11, idFrecuencia);
                    ps.executeUpdate();
                }
                con.commit();
                c.setIdComida(String.valueOf(id));
                return true;
            } catch (SQLException e) {
                con.rollback();
                throw e;
            }
        } catch (SQLException e) {
            System.err.println("[DAO] Error al registrar la comida: " + e.getMessage());
            return false;
        }
    }

    // READ por ID
    public ComidaPreparada obtenerPorId(String idComida) {
        List<ComidaPreparada> lista = consultar("WHERE c.id_comida = ?", idComida);
        return lista.isEmpty() ? null : lista.get(0);
    }

    // READ por hogar
    public List<ComidaPreparada> obtenerPorHogar(String idHogar) {
        return consultar("WHERE q.id_hogar = ? ORDER BY d.numero_dia, c.id_comida", idHogar);
    }

    private List<ComidaPreparada> consultar(String condicion, String parametro) {
        List<ComidaPreparada> lista = new ArrayList<>();
        String sql = "SELECT c.id_comida, q.id_hogar, d.numero_dia, t.nombre AS tipo, c.descripcion, c.valor_pagado, "
                + "c.cantidad, c.domicilio, g.nombre AS categoria, l.nombre AS lugar, f.nombre AS forma, "
                + "fq.nombre AS frecuencia "
                + "FROM comida_preparada c "
                + "JOIN dia_recoleccion d ON d.id_dia = c.id_dia "
                + "JOIN cuadernillo2 q ON q.id_cuadernillo = d.id_cuadernillo "
                + "JOIN tipo_comida t ON t.id_tipo_comida = c.id_tipo_comida "
                + "LEFT JOIN tipo_gasto g ON g.id_tipo_gasto = c.id_tipo_gasto "
                + "LEFT JOIN lugar_compra l ON l.id_lugar_compra = c.id_lugar_compra "
                + "LEFT JOIN forma_adquisicion f ON f.id_forma_adquisicion = c.id_forma_adquisicion "
                + "LEFT JOIN frecuencia_compra fq ON fq.id_frecuencia_compra = c.id_frecuencia_compra " + condicion;
        try (Connection con = ConexionBD.obtenerConexion()) {
            if (con == null) {
                System.err.println("[DAO] No hay conexion con Oracle: no se pudo consultar.");
                return lista;
            }
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, parametro);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        ComidaPreparada m = new ComidaPreparada(rs.getString("id_comida"), rs.getString("id_hogar"),
                                rs.getInt("numero_dia"), rs.getString("tipo"), rs.getString("descripcion"),
                                rs.getDouble("valor_pagado"));
                        m.setCantidad(rs.getInt("cantidad"));
                        m.setDomicilio("S".equals(rs.getString("domicilio")));
                        m.setCategoria(rs.getString("categoria"));
                        m.setLugarCompra(rs.getString("lugar"));
                        m.setFormaAdquisicion(rs.getString("forma"));
                        m.setFrecuenciaCompra(rs.getString("frecuencia"));
                        lista.add(m);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("[DAO] Error al consultar las comidas: " + e.getMessage());
        }
        return lista;
    }

    // UPDATE
    public boolean actualizarComida(ComidaPreparada c) {
        String sql = "UPDATE comida_preparada SET descripcion = ?, valor_pagado = ?, id_dia = ?, id_tipo_comida = ?, "
                + "cantidad = ?, domicilio = ?, id_lugar_compra = ?, id_tipo_gasto = ?, id_forma_adquisicion = ?, "
                + "id_frecuencia_compra = ? WHERE id_comida = ?";
        try (Connection con = ConexionBD.obtenerConexion()) {
            if (con == null) {
                return false;
            }
            con.setAutoCommit(false);
            try {
                long idDia = gastoDAO.obtenerOCrearDia(con, c.getIdHogar(), c.getDia());
                if (idDia < 0) {
                    con.rollback();
                    return false;
                }
                String idLugar = gastoDAO.buscarOCrearCatalogo(con, "lugar_compra", "id_lugar_compra",
                        c.getLugarCompra(), "No especificado", "L");
                String idForma = gastoDAO.buscarOCrearCatalogo(con, "forma_adquisicion", "id_forma_adquisicion",
                        c.getFormaAdquisicion(), "Comprado", "F");
                String idFrecuencia = gastoDAO.idFrecuencia(con, c.getFrecuenciaCompra());
                try (PreparedStatement ps = con.prepareStatement(sql)) {
                    ps.setString(1, c.getEstablecimiento());
                    ps.setDouble(2, c.getValor());
                    ps.setLong(3, idDia);
                    ps.setString(4, codigoTipo(c.getTipo()));
                    ps.setInt(5, c.getCantidad());
                    ps.setString(6, c.isDomicilio() ? "S" : "N");
                    ps.setString(7, idLugar);
                    ps.setString(8, gastoDAO.idTipoGasto(c.getCategoria()));
                    ps.setString(9, idForma);
                    ps.setString(10, idFrecuencia);
                    ps.setLong(11, Long.parseLong(c.getIdComida()));
                    boolean ok = ps.executeUpdate() > 0;
                    con.commit();
                    return ok;
                }
            } catch (SQLException | NumberFormatException e) {
                con.rollback();
                throw e;
            }
        } catch (SQLException | NumberFormatException e) {
            System.err.println("[DAO] Error al actualizar la comida: " + e.getMessage());
            return false;
        }
    }

    // DELETE
    public boolean eliminarComida(String idComida) {
        String sql = "DELETE FROM comida_preparada WHERE id_comida = ?";
        try (Connection con = ConexionBD.obtenerConexion()) {
            if (con == null) {
                return false;
            }
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setLong(1, Long.parseLong(idComida));
                boolean ok = ps.executeUpdate() > 0;
                if (!con.getAutoCommit()) {
                    con.commit();
                }
                return ok;
            }
        } catch (SQLException | NumberFormatException e) {
            System.err.println("[DAO] Error al eliminar la comida: " + e.getMessage());
            return false;
        }
    }
}
