package dao;

import conexion.ConexionBD;
import modelos.GastoDiario;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Acceso a datos de la tabla gasto_diario (Oracle). CRUD completo:
 * Create (registrarGasto), Read (obtener...), Update (actualizarGasto), Delete (eliminarGasto).
 */
public class GastoDiarioDAO {

    private static final String COLUMNAS =
            "id_gasto, id_hogar, dia, categoria, descripcion_producto, cantidad, unidad_medida, valor_pagado, lugar_compra";

    // ---------------------------------------------------------------- CREATE
    /**
     * Inserta un gasto. El id se toma de la secuencia seq_gasto_diario y se
     * deja en gasto.idGasto. Devuelve true solo si realmente se guardo.
     */
    public boolean registrarGasto(GastoDiario gasto) {
        String sqlId = "SELECT seq_gasto_diario.NEXTVAL FROM dual";
        String sql = "INSERT INTO gasto_diario (" + COLUMNAS + ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection con = ConexionBD.obtenerConexion()) {
            if (con == null) {
                System.err.println("[DAO] No hay conexion con Oracle: el gasto NO se guardo.");
                return false;
            }
            long id;
            try (Statement st = con.createStatement(); ResultSet rs = st.executeQuery(sqlId)) {
                rs.next();
                id = rs.getLong(1);
            }
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setLong(1, id);
                ps.setString(2, gasto.getIdHogar());
                ps.setInt(3, gasto.getDia());
                ps.setString(4, gasto.getCategoria());
                ps.setString(5, gasto.getDescripcionProducto());
                ps.setDouble(6, gasto.getCantidad());
                ps.setString(7, gasto.getUnidadMedida());
                ps.setDouble(8, gasto.getValorPagado());
                ps.setString(9, gasto.getLugarCompra());

                boolean ok = ps.executeUpdate() > 0;
                confirmar(con);
                if (ok) {
                    gasto.setIdGasto(String.valueOf(id));
                }
                return ok;
            }
        } catch (SQLException e) {
            System.err.println("[DAO] Error al insertar el gasto en Oracle: " + e.getMessage());
            return false;
        }
    }

    // ------------------------------------------------------------------ READ
    /** Busca un gasto por su id. Devuelve null si no existe o si falla. */
    public GastoDiario obtenerGastoPorId(String idGasto) {
        String sql = "SELECT " + COLUMNAS + " FROM gasto_diario WHERE id_gasto = ?";

        try (Connection con = ConexionBD.obtenerConexion()) {
            if (con == null) {
                System.err.println("[DAO] No hay conexion con Oracle: no se pudo consultar.");
                return null;
            }
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setLong(1, Long.parseLong(idGasto));
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return mapear(rs);
                    }
                }
            }
        } catch (SQLException | NumberFormatException e) {
            System.err.println("[DAO] Error al consultar el gasto: " + e.getMessage());
        }
        return null;
    }

    /** Devuelve todos los gastos de un hogar (lista vacia si no hay o si falla). */
    public List<GastoDiario> obtenerGastosPorHogar(String idHogar) {
        List<GastoDiario> lista = new ArrayList<>();
        String sql = "SELECT " + COLUMNAS + " FROM gasto_diario WHERE id_hogar = ? ORDER BY dia, id_gasto";

        try (Connection con = ConexionBD.obtenerConexion()) {
            if (con == null) {
                System.err.println("[DAO] No hay conexion con Oracle: no se pudo consultar.");
                return lista;
            }
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, idHogar);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        lista.add(mapear(rs));
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("[DAO] Error al consultar gastos en Oracle: " + e.getMessage());
        }
        return lista;
    }

    /** Suma de valor_pagado de un hogar (0 si no hay gastos o si falla). */
    public double obtenerTotalGastosHogar(String idHogar) {
        String sql = "SELECT NVL(SUM(valor_pagado), 0) AS total_gastado FROM gasto_diario WHERE id_hogar = ?";

        try (Connection con = ConexionBD.obtenerConexion()) {
            if (con == null) {
                return 0.0;
            }
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, idHogar);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return rs.getDouble("total_gastado");
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("[DAO] Error al calcular el total de gastos: " + e.getMessage());
        }
        return 0.0;
    }

    // ---------------------------------------------------------------- UPDATE
    /** Actualiza los datos de un gasto existente (se identifica por idGasto). */
    public boolean actualizarGasto(GastoDiario gasto) {
        String sql = "UPDATE gasto_diario SET dia = ?, categoria = ?, descripcion_producto = ?, cantidad = ?, "
                + "unidad_medida = ?, valor_pagado = ?, lugar_compra = ? WHERE id_gasto = ?";

        try (Connection con = ConexionBD.obtenerConexion()) {
            if (con == null) {
                System.err.println("[DAO] No hay conexion con Oracle: el gasto NO se actualizo.");
                return false;
            }
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setInt(1, gasto.getDia());
                ps.setString(2, gasto.getCategoria());
                ps.setString(3, gasto.getDescripcionProducto());
                ps.setDouble(4, gasto.getCantidad());
                ps.setString(5, gasto.getUnidadMedida());
                ps.setDouble(6, gasto.getValorPagado());
                ps.setString(7, gasto.getLugarCompra());
                ps.setLong(8, Long.parseLong(gasto.getIdGasto()));

                boolean ok = ps.executeUpdate() > 0;
                confirmar(con);
                return ok;
            }
        } catch (SQLException | NumberFormatException e) {
            System.err.println("[DAO] Error al actualizar el gasto: " + e.getMessage());
            return false;
        }
    }

    // ---------------------------------------------------------------- DELETE
    /** Elimina un gasto por su id. Devuelve true si se borro una fila. */
    public boolean eliminarGasto(String idGasto) {
        String sql = "DELETE FROM gasto_diario WHERE id_gasto = ?";

        try (Connection con = ConexionBD.obtenerConexion()) {
            if (con == null) {
                System.err.println("[DAO] No hay conexion con Oracle: el gasto NO se elimino.");
                return false;
            }
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setLong(1, Long.parseLong(idGasto));
                boolean ok = ps.executeUpdate() > 0;
                confirmar(con);
                return ok;
            }
        } catch (SQLException | NumberFormatException e) {
            System.err.println("[DAO] Error al eliminar el gasto: " + e.getMessage());
            return false;
        }
    }

    // --------------------------------------------------------------- Ayudas
    private GastoDiario mapear(ResultSet rs) throws SQLException {
        return new GastoDiario(
                String.valueOf(rs.getLong("id_gasto")),
                rs.getString("id_hogar"),
                rs.getInt("dia"),
                rs.getString("categoria"),
                rs.getString("descripcion_producto"),
                rs.getDouble("cantidad"),
                rs.getString("unidad_medida"),
                rs.getDouble("valor_pagado"),
                rs.getString("lugar_compra"));
    }

    private void confirmar(Connection con) throws SQLException {
        if (!con.getAutoCommit()) {
            con.commit();
        }
    }
}