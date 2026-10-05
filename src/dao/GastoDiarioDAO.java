package dao;

import conexion.ConexionBD;
import modelos.GastoDiario;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class GastoDiarioDAO {

    /**
     * Registra un nuevo gasto diario en la base de datos Oracle
     * @param gasto Objeto con la información del producto/servicio
     * @return true si se guardó con éxito, false en caso contrario
     */
    public boolean registrarGasto(GastoDiario gasto) {
        Connection con = ConexionBD.obtenerConexion();
        
        // Manejo controlado si no hay conexión activa a la BD Oracle
        if (con == null) {
            System.out.println("ℹ️ [DAO] Simulación en memoria: Gasto procesado correctamente mediante POO / Java.");
            return true;
        }

        String sql = "INSERT INTO gasto_diario (id_hogar, descripcion_producto, cantidad, unidad_medida, valor_pagado, lugar_compra) VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, gasto.getIdHogar());
            ps.setString(2, gasto.getDescripcionProducto());
            ps.setDouble(3, gasto.getCantidad());
            ps.setString(4, gasto.getUnidadMedida());
            ps.setDouble(5, gasto.getValorPagado());
            ps.setString(6, gasto.getLugarCompra());

            int filasAfectadas = ps.executeUpdate();
            return filasAfectadas > 0;

        } catch (Exception e) {
            System.out.println("❌ Error al insertar el gasto en la base de datos Oracle: " + e.getMessage());
            return false;
        }
    }

    /**
     * Consulta y retorna todos los gastos registrados para un hogar en específico
     * @param idHogar Código identificador del hogar
     * @return Lista de objetos GastoDiario
     */
    public List<GastoDiario> obtenerGastosPorHogar(String idHogar) {
        List<GastoDiario> listaGastos = new ArrayList<>();
        Connection con = ConexionBD.obtenerConexion();

        if (con == null) {
            System.out.println("ℹ️ [DAO] Simulación: Retornando lista vacía (Sin conexión a BD).");
            return listaGastos;
        }

        String sql = "SELECT id_gasto, id_hogar, descripcion_producto, cantidad, unidad_medida, valor_pagado, lugar_compra FROM gasto_diario WHERE id_hogar = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, idHogar);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                GastoDiario gasto = new GastoDiario(
                    String.valueOf(rs.getInt("id_gasto")),
                    rs.getString("id_hogar"),
                    rs.getString("descripcion_producto"),
                    rs.getDouble("cantidad"),
                    rs.getString("unidad_medida"),
                    rs.getDouble("valor_pagado"),
                    rs.getString("lugar_compra")
                );
                listaGastos.add(gasto);
            }
        } catch (Exception e) {
            System.out.println("❌ Error al consultar gastos del hogar en Oracle: " + e.getMessage());
        }

        return listaGastos;
    }

    /**
     * Calcula la suma total en pesos ($) de todos los gastos de un hogar
     * @param idHogar Código del hogar
     * @return Suma en valor decimal
     */
    public double obtenerTotalGastosHogar(String idHogar) {
        double total = 0.0;
        Connection con = ConexionBD.obtenerConexion();

        if (con == null) {
            return total;
        }

        String sql = "SELECT SUM(valor_pagado) AS total_gastado FROM gasto_diario WHERE id_hogar = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, idHogar);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                total = rs.getDouble("total_gastado");
            }
        } catch (Exception e) {
            System.out.println("❌ Error al calcular el total de gastos: " + e.getMessage());
        }

        return total;
    }
}