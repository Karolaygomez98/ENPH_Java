package servidor;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import conexion.ConexionBD;
import modelos.GastoDiario;
import modelos.Hogar;
import servicios.GastoServicio;
import servicios.HogarServicio;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Servidor HTTP minimo (clase del JDK, sin librerias externas) que conecta la
 * interfaz web con Oracle. Recibe las peticiones del navegador y las delega en
 * las clases de servicio (GastoServicio, HogarServicio), que a su vez usan los
 * DAO y JDBC. Solo escucha en 127.0.0.1 (el propio equipo).
 *
 * Rutas:
 *   GET  /api/salud                 -> comprueba la conexion con Oracle
 *   GET  /api/gastos?hogar=ID       -> READ: gastos de un hogar
 *   POST /api/gastos/crear          -> CREATE: guarda un gasto (crea el hogar si no existe)
 *   POST /api/gastos/actualizar     -> UPDATE: cambia categoria, descripcion, dia o valor
 *   POST /api/gastos/eliminar       -> DELETE: elimina un gasto por id
 */
public class ServidorHTTP {

    private static final int PUERTO = 8081;
    private static final GastoServicio GASTOS = new GastoServicio();
    private static final HogarServicio HOGARES = new HogarServicio();

    /** Accion de una ruta: recibe los parametros y devuelve el JSON de respuesta. */
    private interface Accion {
        String ejecutar(Map<String, String> parametros);
    }

    public static void main(String[] args) throws IOException {
        HttpServer servidor = HttpServer.create(new InetSocketAddress("127.0.0.1", PUERTO), 0);

        servidor.createContext("/api/salud", intercambio ->
                responder(intercambio, p -> json(true, "Servidor activo", null, ConexionBD.probarConexion() ? "true" : "false")));
        servidor.createContext("/api/gastos/crear", intercambio -> responder(intercambio, ServidorHTTP::crearGasto));
        servidor.createContext("/api/gastos/actualizar", intercambio -> responder(intercambio, ServidorHTTP::actualizarGasto));
        servidor.createContext("/api/gastos/eliminar", intercambio -> responder(intercambio, ServidorHTTP::eliminarGasto));
        servidor.createContext("/api/gastos", intercambio -> responder(intercambio, ServidorHTTP::listarGastos));

        servidor.start();
        System.out.println("==================================================");
        System.out.println(" Servidor ENPH activo en http://127.0.0.1:" + PUERTO);
        System.out.println(" Deje esta ventana abierta mientras usa la interfaz web.");
        System.out.println("==================================================");
        System.out.println("Conexion con Oracle: " + (ConexionBD.probarConexion() ? "OK" : "SIN CONEXION"));
    }

    // ------------------------------------------------------------ CREATE
    private static String crearGasto(Map<String, String> p) {
        String idHogar = texto(p, "hogar", 20);
        String concepto = texto(p, "concepto", 150);
        if (idHogar.isEmpty() || concepto.isEmpty()) {
            return json(false, "Faltan el hogar o la descripcion del gasto.", null, null);
        }
        double valor;
        double cantidad;
        int dia;
        try {
            valor = Double.parseDouble(p.getOrDefault("valor", ""));
            dia = Integer.parseInt(p.getOrDefault("dia", "1"));
            String cant = p.getOrDefault("cantidad", "").trim();
            cantidad = cant.isEmpty() ? 1.0 : Double.parseDouble(cant);
        } catch (NumberFormatException e) {
            return json(false, "El valor, el dia y la cantidad deben ser numericos.", null, null);
        }
        if (!asegurarHogar(p, idHogar)) {
            return json(false, "No se pudo registrar el hogar en Oracle (revise la conexion).", null, null);
        }
        String categoria = texto(p, "categoria", 50);
        // Si la interfaz no envia unidad o lugar, se usan valores por defecto.
        String unidad = texto(p, "unidad", 30);
        String lugar = texto(p, "lugar", 100);
        GastoDiario gasto = new GastoDiario(null, idHogar, dia, categoria.isEmpty() ? null : categoria,
                concepto, cantidad, unidad.isEmpty() ? "Unidad" : unidad, valor,
                lugar.isEmpty() ? "No especificado" : lugar);
        if (!GASTOS.guardarNuevoGasto(gasto)) {
            return json(false, "Oracle no guardo el gasto (dia 1 a 14 y valor mayor a 0).", null, null);
        }
        return json(true, "Gasto guardado en Oracle", gasto.getIdGasto(), null);
    }

    /** La interfaz maneja hogares propios: si el hogar aun no esta en Oracle, se crea. */
    private static boolean asegurarHogar(Map<String, String> p, String idHogar) {
        if (HOGARES.consultarHogar(idHogar) != null) {
            return true;
        }
        String[] partes = idHogar.split("-");
        String codigo = partes.length >= 2 ? partes[1] : "00000";
        String jefe = texto(p, "jefe", 100);
        String direccion = texto(p, "direccion", 150);
        Hogar hogar = new Hogar(idHogar, codigo, jefe.isEmpty() ? "Sin dato" : jefe,
                direccion.isEmpty() ? "Sin dato" : direccion, "En proceso");
        return HOGARES.guardarNuevoHogar(hogar);
    }

    // ------------------------------------------------------------ UPDATE
    private static String actualizarGasto(Map<String, String> p) {
        GastoDiario gasto = GASTOS.consultarGasto(p.get("id"));
        if (gasto == null) {
            return json(false, "noexiste", null, null);
        }
        try {
            gasto.setDia(Integer.parseInt(p.getOrDefault("dia", String.valueOf(gasto.getDia()))));
            gasto.setValorPagado(Double.parseDouble(p.getOrDefault("valor", String.valueOf(gasto.getValorPagado()))));
            String cant = p.getOrDefault("cantidad", "").trim();
            if (!cant.isEmpty()) {
                gasto.setCantidad(Double.parseDouble(cant));
            }
        } catch (NumberFormatException e) {
            return json(false, "El valor, el dia y la cantidad deben ser numericos.", null, null);
        }
        String unidad = texto(p, "unidad", 30);
        if (!unidad.isEmpty()) {
            gasto.setUnidadMedida(unidad);
        }
        String lugar = texto(p, "lugar", 100);
        gasto.setLugarCompra(lugar.isEmpty() ? "No especificado" : lugar);
        String concepto = texto(p, "concepto", 150);
        if (!concepto.isEmpty()) {
            gasto.setDescripcionProducto(concepto);
        }
        String categoria = texto(p, "categoria", 50);
        gasto.setCategoria(categoria.isEmpty() ? null : categoria);

        if (!GASTOS.actualizarGasto(gasto)) {
            return json(false, "Oracle no actualizo el gasto.", null, null);
        }
        return json(true, "Gasto actualizado en Oracle", gasto.getIdGasto(), null);
    }

    // ------------------------------------------------------------ DELETE
    private static String eliminarGasto(Map<String, String> p) {
        if (!GASTOS.eliminarGasto(p.get("id"))) {
            return json(false, "Oracle no elimino el gasto.", null, null);
        }
        return json(true, "Gasto eliminado de Oracle", p.get("id"), null);
    }

    // ------------------------------------------------------------ READ
    private static String listarGastos(Map<String, String> p) {
        List<GastoDiario> lista = GASTOS.consultarGastosHogar(p.get("hogar"));
        StringBuilder sb = new StringBuilder("{\"ok\":true,\"gastos\":[");
        for (int i = 0; i < lista.size(); i++) {
            GastoDiario g = lista.get(i);
            if (i > 0) sb.append(',');
            sb.append("{\"id\":\"").append(esc(g.getIdGasto())).append("\",\"dia\":").append(g.getDia())
              .append(",\"categoria\":\"").append(esc(g.getCategoria())).append("\",\"concepto\":\"")
              .append(esc(g.getDescripcionProducto())).append("\",\"cantidad\":").append(g.getCantidad())
              .append(",\"unidad\":\"").append(esc(g.getUnidadMedida())).append("\",\"lugar\":\"")
              .append(esc(g.getLugarCompra())).append("\",\"valor\":").append(g.getValorPagado()).append('}');
        }
        return sb.append("]}").toString();
    }

    // ------------------------------------------------------------ utilidades
    /** Lee los parametros (de la URL o del cuerpo), ejecuta la accion y responde con CORS. */
    private static void responder(HttpExchange intercambio, Accion accion) throws IOException {
        intercambio.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        intercambio.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");
        intercambio.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        intercambio.getResponseHeaders().add("Content-Type", "application/json; charset=utf-8");

        if ("OPTIONS".equalsIgnoreCase(intercambio.getRequestMethod())) {
            intercambio.sendResponseHeaders(204, -1);
            intercambio.close();
            return;
        }

        String respuesta;
        try {
            Map<String, String> parametros = new HashMap<>();
            leerParametros(intercambio.getRequestURI().getRawQuery(), parametros);
            String cuerpo = new String(intercambio.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            leerParametros(cuerpo, parametros);
            respuesta = accion.ejecutar(parametros);
        } catch (Exception e) {
            System.err.println("[Servidor] Error: " + e.getMessage());
            respuesta = json(false, "Error interno del servidor.", null, null);
        }

        // Registro en consola: permite ver cada peticion que llega desde la interfaz web
        System.out.println("[Servidor] " + intercambio.getRequestMethod() + " "
                + intercambio.getRequestURI().getPath() + " -> " + respuesta);

        byte[] bytes = respuesta.getBytes(StandardCharsets.UTF_8);
        intercambio.sendResponseHeaders(200, bytes.length);
        try (OutputStream os = intercambio.getResponseBody()) {
            os.write(bytes);
        }
    }

    /** Convierte "a=1&b=dos" en un mapa, decodificando los caracteres especiales. */
    private static void leerParametros(String crudo, Map<String, String> destino) {
        if (crudo == null || crudo.isEmpty()) return;
        for (String par : crudo.split("&")) {
            int igual = par.indexOf('=');
            if (igual < 0) continue;
            String clave = URLDecoder.decode(par.substring(0, igual), StandardCharsets.UTF_8);
            String valor = URLDecoder.decode(par.substring(igual + 1), StandardCharsets.UTF_8);
            destino.put(clave, valor);
        }
    }

    /** Texto limpio y recortado al maximo de la columna de la tabla. */
    private static String texto(Map<String, String> p, String clave, int maximo) {
        String t = p.getOrDefault(clave, "").trim();
        return t.length() > maximo ? t.substring(0, maximo) : t;
    }

    private static String json(boolean ok, String mensaje, String id, String oracle) {
        StringBuilder sb = new StringBuilder("{\"ok\":").append(ok).append(",\"mensaje\":\"").append(esc(mensaje)).append('"');
        if (id != null) sb.append(",\"id\":\"").append(esc(id)).append('"');
        if (oracle != null) sb.append(",\"oracle\":").append(oracle);
        return sb.append('}').toString();
    }

    private static String esc(String s) {
        return s == null ? "" : s.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", " ").replace("\r", " ");
    }
}