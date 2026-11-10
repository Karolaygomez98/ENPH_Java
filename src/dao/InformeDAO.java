package dao;

import conexion.ConexionBD;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Consultas agregadas (GROUP BY) para el informe gerencial del Supervisor.
 * Une las compras (gasto_diario) y las comidas fuera del hogar (comida_preparada)
 * en un solo conjunto y lo agrupa por distintos criterios.
 */
public class InformeDAO {

    /** Vista de Oracle (database.sql / objetos_informe.sql): dia, valor, origen, tipo, lugar, hogar, estado y fp. */
    private static final String BASE_VISTA =
            "SELECT dia, valor, origen, tipo, lugar, hogar, estado, fp FROM v_gasto_unificado";

    /** Misma consulta escrita completa: se usa solo si la vista aun no existe en la base de datos. */
    private static final String BASE_DIRECTA =
            "SELECT d.numero_dia AS dia, g.valor_pagado AS valor, 'Compras' AS origen, "
            + "tg.nombre AS tipo, lc.nombre AS lugar, c.id_hogar AS hogar, c.estado AS estado, c.id_forma_pago AS fp "
            + "FROM gasto_diario g JOIN dia_recoleccion d ON d.id_dia = g.id_dia "
            + "JOIN cuadernillo2 c ON c.id_cuadernillo = d.id_cuadernillo "
            + "JOIN tipo_gasto tg ON tg.id_tipo_gasto = g.id_tipo_gasto "
            + "JOIN lugar_compra lc ON lc.id_lugar_compra = g.id_lugar_compra "
            + "UNION ALL "
            + "SELECT d.numero_dia, m.valor_pagado, 'Comidas fuera del hogar', "
            + "NVL(tg.nombre, 'Hogar'), NVL(lc.nombre, 'No especificado'), c.id_hogar, c.estado, c.id_forma_pago "
            + "FROM comida_preparada m JOIN dia_recoleccion d ON d.id_dia = m.id_dia "
            + "JOIN cuadernillo2 c ON c.id_cuadernillo = d.id_cuadernillo "
            + "LEFT JOIN tipo_gasto tg ON tg.id_tipo_gasto = m.id_tipo_gasto "
            + "LEFT JOIN lugar_compra lc ON lc.id_lugar_compra = m.id_lugar_compra";

    /** Una barra o porcion: etiqueta y valor. */
    public static class Dato {
        public final String etiqueta;
        public final double valor;

        public Dato(String etiqueta, double valor) {
            this.etiqueta = etiqueta;
            this.valor = valor;
        }
    }

    /** Todo lo que muestra el informe. */
    public static class Informe {
        public int hogares;
        public int hogaresCerrados;
        public int hogaresRevisados;
        public int registros;
        public double total;
        public List<Dato> porLugar = new ArrayList<>();
        public List<Dato> porOrigen = new ArrayList<>();
        public List<Dato> porTipo = new ArrayList<>();
        public List<Dato> porDia = new ArrayList<>();
        public List<Dato> porPago = new ArrayList<>();
        public List<Dato> estados = new ArrayList<>();
    }

    /** Arma el informe; si soloCerrados es true usa unicamente los cuadernillos Completados. Null si no hay conexion. */
    public Informe generar(boolean soloCerrados) {
        Informe inf = generar(soloCerrados, BASE_VISTA);
        if (inf == null && !vistaExiste()) {
            System.err.println("[DAO] La vista v_gasto_unificado no existe: se usa la consulta directa.");
            inf = generar(soloCerrados, BASE_DIRECTA);
        }
        return inf;
    }

    /** Indica si la vista v_gasto_unificado existe y se puede consultar. */
    private boolean vistaExiste() {
        try (Connection con = ConexionBD.obtenerConexion()) {
            if (con == null) {
                return true; // sin conexion: no tiene sentido reintentar con otra consulta
            }
            try (PreparedStatement ps = con.prepareStatement("SELECT 1 FROM v_gasto_unificado WHERE 1 = 0")) {
                ps.executeQuery().close();
                return true;
            }
        } catch (SQLException e) {
            return false;
        }
    }

    private Informe generar(boolean soloCerrados, String BASE) {
        String filtro = soloCerrados ? "WHERE u.estado = 'Completado' " : "";
        Informe inf = new Informe();
        try (Connection con = ConexionBD.obtenerConexion()) {
            if (con == null) {
                return null;
            }
            inf.hogares = contar(con, "SELECT COUNT(*) FROM cuadernillo2");
            inf.hogaresCerrados = contar(con, "SELECT COUNT(*) FROM cuadernillo2 WHERE estado = 'Completado'");
            inf.hogaresRevisados = contar(con, "SELECT COUNT(*) FROM cuadernillo2 WHERE revisado = 'S'");
            inf.estados = serie(con, "SELECT estado, COUNT(*) FROM cuadernillo2 GROUP BY estado ORDER BY 2 DESC");
            inf.porLugar = serie(con, "SELECT u.lugar, SUM(u.valor) FROM (" + BASE + ") u " + filtro
                    + "GROUP BY u.lugar ORDER BY 2 DESC");
            inf.porOrigen = serie(con, "SELECT u.origen, SUM(u.valor) FROM (" + BASE + ") u " + filtro
                    + "GROUP BY u.origen ORDER BY 2 DESC");
            inf.porTipo = serie(con, "SELECT u.tipo, SUM(u.valor) FROM (" + BASE + ") u " + filtro
                    + "GROUP BY u.tipo ORDER BY 2 DESC");
            inf.porDia = serie(con, "SELECT 'D' || u.dia, SUM(u.valor) FROM (" + BASE + ") u " + filtro
                    + "GROUP BY u.dia ORDER BY u.dia");
            inf.porPago = serie(con, "SELECT NVL(f.nombre, 'Sin definir'), COUNT(DISTINCT u.hogar) FROM (" + BASE + ") u "
                    + "LEFT JOIN forma_pago f ON f.id_forma_pago = u.fp " + filtro
                    + "GROUP BY NVL(f.nombre, 'Sin definir') ORDER BY 2 DESC");
            inf.registros = contar(con, "SELECT COUNT(*) FROM (" + BASE + ") u " + filtro);
            for (Dato d : inf.porOrigen) {
                inf.total += d.valor;
            }
        } catch (SQLException e) {
            System.err.println("[DAO] Error al generar el informe gerencial: " + e.getMessage());
            return null;
        }
        return inf;
    }

    private int contar(Connection con, String sql) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    private List<Dato> serie(Connection con, String sql) throws SQLException {
        List<Dato> lista = new ArrayList<>();
        try (PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(new Dato(rs.getString(1), rs.getDouble(2)));
            }
        }
        return lista;
    }
}
