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
 * Acceso a datos del gasto diario sobre el modelo relacional de 16 tablas.
 * Un gasto se guarda en gasto_diario y se enlaza con dia_recoleccion (que a su vez
 * pertenece al cuadernillo2 del hogar) y con los catalogos unidad_medida,
 * lugar_compra y tipo_gasto. La clase GastoDiario (modelo) conserva sus campos:
 * categoria = nombre del tipo de gasto (Hogar / Personal), unidadMedida y lugarCompra
 * = nombres de los catalogos.
 * CRUD completo: Create (registrarGasto), Read (obtener...), Update (actualizarGasto),
 * Delete (eliminarGasto).
 */
public class GastoDiarioDAO {

    /** Consulta base: une el gasto con su dia, su cuadernillo (hogar) y los catalogos. */
    private static final String SELECT_GASTO =
            "SELECT g.id_gasto, c.id_hogar, d.numero_dia AS dia, t.nombre AS categoria, "
          + "g.articulo AS descripcion_producto, g.cantidad, u.nombre AS unidad_medida, "
          + "g.valor_pagado, l.nombre AS lugar_compra, f.nombre AS forma_adquisicion, q.nombre AS frecuencia, g.equivalencia "
          + "FROM gasto_diario g "
          + "JOIN dia_recoleccion d ON d.id_dia = g.id_dia "
          + "JOIN cuadernillo2 c ON c.id_cuadernillo = d.id_cuadernillo "
          + "JOIN unidad_medida u ON u.id_unidad_medida = g.id_unidad_medida "
          + "JOIN lugar_compra l ON l.id_lugar_compra = g.id_lugar_compra "
          + "JOIN tipo_gasto t ON t.id_tipo_gasto = g.id_tipo_gasto "
          + "JOIN forma_adquisicion f ON f.id_forma_adquisicion = g.id_forma_adquisicion "
          + "LEFT JOIN frecuencia_compra q ON q.id_frecuencia_compra = g.id_frecuencia_compra ";

    /** Para comparar nombres sin importar mayusculas ni tildes (aeiou y n con tilde). */
    private static final String CON_TILDE = "áéíóúÁÉÍÓÚñÑ";
    private static final String SIN_TILDE = "aeiouAEIOUnN";

    // ---------------------------------------------------------------- CREATE
    /**
     * Inserta un gasto. Crea el dia de recoleccion si aun no existe y busca (o crea) la
     * unidad y el lugar de compra en sus catalogos. El id se toma de seq_gasto_diario y se
     * deja en gasto.idGasto. Devuelve true solo si realmente se guardo.
     */
    public boolean registrarGasto(GastoDiario gasto) {
        try (Connection con = ConexionBD.obtenerConexion()) {
            if (con == null) {
                System.err.println("[DAO] No hay conexion con Oracle: el gasto NO se guardo.");
                return false;
            }
            con.setAutoCommit(false);
            try {
                long idDia = obtenerOCrearDia(con, gasto.getIdHogar(), gasto.getDia());
                if (idDia < 0) {
                    System.err.println("[DAO] El hogar " + gasto.getIdHogar() + " no tiene cuadernillo en Oracle.");
                    con.rollback();
                    return false;
                }
                String idUnidad = buscarOCrearCatalogo(con, "unidad_medida", "id_unidad_medida",
                        gasto.getUnidadMedida(), "Unidad", "U");
                String idLugar = buscarOCrearCatalogo(con, "lugar_compra", "id_lugar_compra",
                        gasto.getLugarCompra(), "No especificado", "L");
                String idForma = buscarOCrearCatalogo(con, "forma_adquisicion", "id_forma_adquisicion",
                        gasto.getFormaAdquisicion(), "Comprado", "F");
                String idFrecuencia = idFrecuencia(con, gasto.getFrecuenciaCompra());
                long id;
                try (Statement st = con.createStatement();
                     ResultSet rs = st.executeQuery("SELECT seq_gasto_diario.NEXTVAL FROM dual")) {
                    rs.next();
                    id = rs.getLong(1);
                }
                String sql = "INSERT INTO gasto_diario (id_gasto, articulo, cantidad, valor_pagado, id_dia, "
                        + "id_unidad_medida, id_forma_adquisicion, id_lugar_compra, id_tipo_gasto, id_frecuencia_compra, equivalencia) "
                        + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                try (PreparedStatement ps = con.prepareStatement(sql)) {
                    ps.setLong(1, id);
                    ps.setString(2, gasto.getDescripcionProducto());
                    ps.setDouble(3, gasto.getCantidad());
                    ps.setDouble(4, gasto.getValorPagado());
                    ps.setLong(5, idDia);
                    ps.setString(6, idUnidad);
                    ps.setString(7, idForma);
                    ps.setString(8, idLugar);
                    ps.setString(9, idTipoGasto(gasto.getCategoria()));
                    ps.setString(10, idFrecuencia);
                    if (gasto.getEquivalencia() == null) {
                        ps.setNull(11, java.sql.Types.NUMERIC);
                    } else {
                        ps.setDouble(11, gasto.getEquivalencia());
                    }
                    boolean ok = ps.executeUpdate() > 0;
                    con.commit();
                    if (ok) {
                        gasto.setIdGasto(String.valueOf(id));
                    }
                    return ok;
                }
            } catch (SQLException e) {
                con.rollback();
                throw e;
            }
        } catch (SQLException e) {
            System.err.println("[DAO] Error al insertar el gasto en Oracle: " + e.getMessage());
            return false;
        }
    }

    // ------------------------------------------------------------------ READ
    /** Busca un gasto por su id. Devuelve null si no existe o si falla. */
    public GastoDiario obtenerGastoPorId(String idGasto) {
        String sql = SELECT_GASTO + "WHERE g.id_gasto = ?";
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
        return buscarGastos(idHogar, null, null, null);
    }

    /**
     * Busqueda con filtros opcionales (RF18): hogar, dia, tipo de gasto y articulo.
     * Los filtros en null o vacios se ignoran. Todos los valores viajan como
     * parametros del PreparedStatement (no se concatenan en el SQL).
     */
    public List<GastoDiario> buscarGastos(String idHogar, Integer dia, String categoria, String articulo) {
        List<GastoDiario> lista = new ArrayList<>();
        StringBuilder sql = new StringBuilder(SELECT_GASTO).append("WHERE 1 = 1");
        List<Object> parametros = new ArrayList<>();
        if (idHogar != null && !idHogar.isEmpty()) {
            sql.append(" AND c.id_hogar = ?");
            parametros.add(idHogar);
        }
        if (dia != null) {
            sql.append(" AND d.numero_dia = ?");
            parametros.add(dia);
        }
        if (categoria != null && !categoria.isEmpty()) {
            sql.append(" AND UPPER(t.nombre) LIKE ?");
            parametros.add("%" + categoria.toUpperCase() + "%");
        }
        if (articulo != null && !articulo.isEmpty()) {
            sql.append(" AND UPPER(g.articulo) LIKE ?");
            parametros.add("%" + articulo.toUpperCase() + "%");
        }
        sql.append(" ORDER BY c.id_hogar, d.numero_dia, g.id_gasto");
        try (Connection con = ConexionBD.obtenerConexion()) {
            if (con == null) {
                System.err.println("[DAO] No hay conexion con Oracle: no se pudo buscar.");
                return lista;
            }
            try (PreparedStatement ps = con.prepareStatement(sql.toString())) {
                for (int i = 0; i < parametros.size(); i++) {
                    ps.setObject(i + 1, parametros.get(i));
                }
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        lista.add(mapear(rs));
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("[DAO] Error al buscar gastos en Oracle: " + e.getMessage());
        }
        return lista;
    }

    /**
     * Reporte agrupado de un hogar (RF19). "tipo" agrupa por tipo de gasto (Hogar / Personal),
     * "lugar" por lugar de compra y "dia" por dia. Cada fila es {etiqueta, cantidad de gastos, total}.
     * El SQL se elige de una lista fija; el hogar viaja como parametro.
     */
    public List<Object[]> agruparGastos(String idHogar, String agrupar) {
        List<Object[]> filas = new ArrayList<>();
        String desde = "FROM gasto_diario g "
                + "JOIN dia_recoleccion d ON d.id_dia = g.id_dia "
                + "JOIN cuadernillo2 c ON c.id_cuadernillo = d.id_cuadernillo ";
        String sql;
        if ("tipo".equals(agrupar)) {
            sql = "SELECT t.nombre AS etiqueta, COUNT(*) AS n, SUM(g.valor_pagado) AS total " + desde
                + "JOIN tipo_gasto t ON t.id_tipo_gasto = g.id_tipo_gasto "
                + "WHERE c.id_hogar = ? GROUP BY t.nombre ORDER BY total DESC";
        } else if ("lugar".equals(agrupar)) {
            sql = "SELECT l.nombre AS etiqueta, COUNT(*) AS n, SUM(g.valor_pagado) AS total " + desde
                + "JOIN lugar_compra l ON l.id_lugar_compra = g.id_lugar_compra "
                + "WHERE c.id_hogar = ? GROUP BY l.nombre ORDER BY total DESC";
        } else if ("dia".equals(agrupar)) {
            sql = "SELECT TO_CHAR(d.numero_dia) AS etiqueta, COUNT(*) AS n, SUM(g.valor_pagado) AS total " + desde
                + "WHERE c.id_hogar = ? GROUP BY d.numero_dia ORDER BY d.numero_dia";
        } else {
            return filas;
        }
        try (Connection con = ConexionBD.obtenerConexion()) {
            if (con == null) {
                System.err.println("[DAO] No hay conexion con Oracle: no se pudo generar el reporte.");
                return filas;
            }
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, idHogar);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        filas.add(new Object[] { rs.getString("etiqueta"), rs.getInt("n"), rs.getDouble("total") });
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("[DAO] Error al generar el reporte: " + e.getMessage());
        }
        return filas;
    }

    /** Suma de valor_pagado de un hogar (0 si no hay gastos o si falla). */
    public double obtenerTotalGastosHogar(String idHogar) {
        String sql = "SELECT NVL(SUM(g.valor_pagado), 0) AS total_gastado FROM gasto_diario g "
                + "JOIN dia_recoleccion d ON d.id_dia = g.id_dia "
                + "JOIN cuadernillo2 c ON c.id_cuadernillo = d.id_cuadernillo WHERE c.id_hogar = ?";
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
        try (Connection con = ConexionBD.obtenerConexion()) {
            if (con == null) {
                System.err.println("[DAO] No hay conexion con Oracle: el gasto NO se actualizo.");
                return false;
            }
            con.setAutoCommit(false);
            try {
                long idDia = obtenerOCrearDia(con, gasto.getIdHogar(), gasto.getDia());
                if (idDia < 0) {
                    con.rollback();
                    return false;
                }
                String idUnidad = buscarOCrearCatalogo(con, "unidad_medida", "id_unidad_medida",
                        gasto.getUnidadMedida(), "Unidad", "U");
                String idLugar = buscarOCrearCatalogo(con, "lugar_compra", "id_lugar_compra",
                        gasto.getLugarCompra(), "No especificado", "L");
                String idForma = buscarOCrearCatalogo(con, "forma_adquisicion", "id_forma_adquisicion",
                        gasto.getFormaAdquisicion(), "Comprado", "F");
                String idFrecuencia = idFrecuencia(con, gasto.getFrecuenciaCompra());
                String sql = "UPDATE gasto_diario SET articulo = ?, cantidad = ?, valor_pagado = ?, id_dia = ?, "
                        + "id_unidad_medida = ?, id_lugar_compra = ?, id_tipo_gasto = ?, "
                        + "id_forma_adquisicion = ?, id_frecuencia_compra = ?, equivalencia = ? WHERE id_gasto = ?";
                try (PreparedStatement ps = con.prepareStatement(sql)) {
                    ps.setString(1, gasto.getDescripcionProducto());
                    ps.setDouble(2, gasto.getCantidad());
                    ps.setDouble(3, gasto.getValorPagado());
                    ps.setLong(4, idDia);
                    ps.setString(5, idUnidad);
                    ps.setString(6, idLugar);
                    ps.setString(7, idTipoGasto(gasto.getCategoria()));
                    ps.setString(8, idForma);
                    ps.setString(9, idFrecuencia);
                    if (gasto.getEquivalencia() == null) {
                        ps.setNull(10, java.sql.Types.NUMERIC);
                    } else {
                        ps.setDouble(10, gasto.getEquivalencia());
                    }
                    ps.setLong(11, Long.parseLong(gasto.getIdGasto()));
                    boolean ok = ps.executeUpdate() > 0;
                    con.commit();
                    return ok;
                }
            } catch (SQLException | NumberFormatException e) {
                con.rollback();
                throw e;
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

    // ------------------------------------------------------------ RF13
    /**
     * Pregunta a Oracle (funcion fn_categoria_excluida) si el articulo pertenece a una categoria
     * que no entra al Cuadernillo 2. Devuelve el nombre de la categoria o null si no esta excluido
     * (tambien null si la funcion no existe todavia en la base).
     */
    public String categoriaExcluida(String articulo) {
        try (Connection con = ConexionBD.obtenerConexion()) {
            if (con == null) {
                return null;
            }
            try (PreparedStatement ps = con.prepareStatement("SELECT fn_categoria_excluida(?) FROM dual")) {
                ps.setString(1, articulo);
                try (ResultSet rs = ps.executeQuery()) {
                    return rs.next() ? rs.getString(1) : null;
                }
            }
        } catch (SQLException e) {
            System.err.println("[DAO] No se pudo consultar fn_categoria_excluida: " + e.getMessage());
            return null;
        }
    }

    // --------------------------------------------------------------- Ayudas
    /** Devuelve el id del dia de recoleccion del hogar; si no existe lo crea. -1 si el hogar no tiene cuadernillo. */
    long obtenerOCrearDia(Connection con, String idHogar, int numeroDia) throws SQLException {
        String buscar = "SELECT d.id_dia FROM dia_recoleccion d "
                + "JOIN cuadernillo2 c ON c.id_cuadernillo = d.id_cuadernillo "
                + "WHERE c.id_hogar = ? AND d.numero_dia = ?";
        try (PreparedStatement ps = con.prepareStatement(buscar)) {
            ps.setString(1, idHogar);
            ps.setInt(2, numeroDia);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
            }
        }
        String idCuadernillo = null;
        try (PreparedStatement ps = con.prepareStatement("SELECT id_cuadernillo FROM cuadernillo2 WHERE id_hogar = ?")) {
            ps.setString(1, idHogar);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    idCuadernillo = rs.getString(1);
                }
            }
        }
        if (idCuadernillo == null) {
            return -1;
        }
        // La fecha y el id del dia los completa el disparador trg_dia_recoleccion_id
        try (PreparedStatement ps = con.prepareStatement(
                "INSERT INTO dia_recoleccion (numero_dia, id_cuadernillo) VALUES (?, ?)")) {
            ps.setInt(1, numeroDia);
            ps.setString(2, idCuadernillo);
            ps.executeUpdate();
        }
        try (PreparedStatement ps = con.prepareStatement(buscar)) {
            ps.setString(1, idHogar);
            ps.setInt(2, numeroDia);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getLong(1) : -1;
            }
        }
    }

    /**
     * Busca un nombre en un catalogo (sin importar mayusculas ni tildes) y devuelve su id.
     * Si no existe, lo agrega con un id nuevo (prefijo + consecutivo, ej. L1, L2). Los
     * nombres de tabla y columna son constantes internas, nunca texto del usuario.
     */
    String buscarOCrearCatalogo(Connection con, String tabla, String columnaId, String nombre,
                                        String porDefecto, String prefijo) throws SQLException {
        String limpio = (nombre == null || nombre.trim().isEmpty()) ? porDefecto : nombre.trim();
        String buscar = "SELECT " + columnaId + " FROM " + tabla + " WHERE UPPER(TRANSLATE(TRIM(nombre), '"
                + CON_TILDE + "', '" + SIN_TILDE + "')) = UPPER(TRANSLATE(TRIM(?), '" + CON_TILDE + "', '" + SIN_TILDE + "'))";
        try (PreparedStatement ps = con.prepareStatement(buscar)) {
            ps.setString(1, limpio);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString(1);
                }
            }
        }
        String siguiente = "SELECT NVL(MAX(CASE WHEN REGEXP_LIKE(" + columnaId + ", '^" + prefijo + "[0-9]+$') "
                + "THEN TO_NUMBER(SUBSTR(" + columnaId + ", 2)) END), 0) + 1 FROM " + tabla;
        long n;
        try (Statement st = con.createStatement(); ResultSet rs = st.executeQuery(siguiente)) {
            rs.next();
            n = rs.getLong(1);
        }
        String nuevoId = prefijo + n;
        try (PreparedStatement ps = con.prepareStatement("INSERT INTO " + tabla + " (" + columnaId + ", nombre) VALUES (?, ?)")) {
            ps.setString(1, nuevoId);
            ps.setString(2, limpio.length() > 100 ? limpio.substring(0, 100) : limpio);
            ps.executeUpdate();
        }
        return nuevoId;
    }

    /** Tipo de gasto del modelo (RF12): PER si el texto dice "personal"; en cualquier otro caso HOG (hogar). */
    String idTipoGasto(String categoria) {
        return (categoria != null && categoria.toLowerCase().contains("personal")) ? "PER" : "HOG";
    }

    /** Id de la frecuencia de compra por su nombre; null (sin dato) si viene vacio o "No aplica". */
    String idFrecuencia(Connection con, String nombre) throws SQLException {
        if (nombre == null || nombre.trim().isEmpty() || nombre.trim().equalsIgnoreCase("No aplica")) {
            return null;
        }
        return buscarOCrearCatalogo(con, "frecuencia_compra", "id_frecuencia_compra", nombre, "Diario", "Q");
    }

    private GastoDiario mapear(ResultSet rs) throws SQLException {
        GastoDiario g = mapearBase(rs);
        g.setFormaAdquisicion(rs.getString("forma_adquisicion"));
        g.setFrecuenciaCompra(rs.getString("frecuencia"));
        double eq = rs.getDouble("equivalencia");
        g.setEquivalencia(rs.wasNull() ? null : eq);
        return g;
    }

    private GastoDiario mapearBase(ResultSet rs) throws SQLException {
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
