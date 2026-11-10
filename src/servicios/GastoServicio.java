package servicios;

import dao.GastoDiarioDAO;
import modelos.GastoDiario;

import java.util.List;

/**
 * Capa de logica de negocio: valida los datos y luego llama al DAO.
 */
public class GastoServicio {

    private final GastoDiarioDAO gastoDAO;

    public GastoServicio() {
        this.gastoDAO = new GastoDiarioDAO();
    }

    /** Devuelve null si el gasto es valido, o el mensaje con el motivo si no lo es. */
    private String validar(GastoDiario gasto) {
        if (gasto == null) {
            return "El objeto gasto no puede ser nulo.";
        }
        if (gasto.getIdHogar() == null || gasto.getIdHogar().trim().isEmpty()) {
            return "El ID del hogar es obligatorio.";
        }
        if (gasto.getDia() < 1 || gasto.getDia() > 14) {
            return "El dia debe estar entre 1 y 14.";
        }
        if (gasto.getDescripcionProducto() == null || gasto.getDescripcionProducto().trim().isEmpty()) {
            return "La descripcion del producto es obligatoria.";
        }
        if (gasto.getCantidad() <= 0) {
            return "La cantidad debe ser mayor a 0.";
        }
        if (gasto.getUnidadMedida() == null || gasto.getUnidadMedida().trim().isEmpty()) {
            return "La unidad de medida es obligatoria.";
        }
        if (gasto.getValorPagado() <= 0) {
            return "El valor pagado debe ser mayor a 0.";
        }
        if (gasto.getLugarCompra() == null || gasto.getLugarCompra().trim().isEmpty()) {
            return "El lugar de compra es obligatorio.";
        }
        return null;
    }

    /** RF12: los gastos personales solo se registran en los primeros 7 dias del periodo. */
    public static final int DIAS_GASTO_PERSONAL = 7;

    /**
     * Reglas de negocio del DANE (RF12 y RF13). Devuelve el mensaje si el gasto las incumple,
     * o null si cumple. La base de datos las vuelve a verificar con disparadores.
     */
    public String reglasNegocio(GastoDiario gasto) {
        if (gasto.getCategoria() != null && gasto.getCategoria().toLowerCase().contains("personal")
                && gasto.getDia() > DIAS_GASTO_PERSONAL) {
            return "Los gastos personales solo se registran en los primeros " + DIAS_GASTO_PERSONAL + " dias.";
        }
        String excluida = gastoDAO.categoriaExcluida(gasto.getDescripcionProducto());
        if (excluida != null) {
            return "No se registra: \"" + excluida + "\" es una categoria excluida del Cuadernillo 2.";
        }
        return null;
    }

    private boolean idValido(String idGasto) {
        if (idGasto == null || idGasto.trim().isEmpty()) {
            return false;
        }
        try {
            Long.parseLong(idGasto.trim());
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /** CREATE: valida y registra un gasto nuevo. */
    public boolean guardarNuevoGasto(GastoDiario gasto) {
        String error = validar(gasto);
        if (error != null) {
            System.out.println("[Servicio] Validacion: " + error);
            return false;
        }
        return gastoDAO.registrarGasto(gasto);
    }

    /** READ: un gasto por id (null si no existe). */
    public GastoDiario consultarGasto(String idGasto) {
        if (!idValido(idGasto)) {
            System.out.println("[Servicio] Validacion: el ID del gasto debe ser numerico.");
            return null;
        }
        return gastoDAO.obtenerGastoPorId(idGasto.trim());
    }

    /** READ: todos los gastos de un hogar. */
    public List<GastoDiario> consultarGastosHogar(String idHogar) {
        if (idHogar == null || idHogar.trim().isEmpty()) {
            System.out.println("[Servicio] Validacion: el ID del hogar es obligatorio para consultar.");
            return List.of();
        }
        return gastoDAO.obtenerGastosPorHogar(idHogar);
    }

    /** READ con filtros (RF18): hogar, dia (1 a 14), categoria y articulo; todos opcionales. */
    public List<GastoDiario> buscarGastos(String idHogar, Integer dia, String categoria, String articulo) {
        if (dia != null && (dia < 1 || dia > 14)) {
            System.out.println("[Servicio] Validacion: el dia debe estar entre 1 y 14.");
            return List.of();
        }
        return gastoDAO.buscarGastos(limpiar(idHogar), dia, limpiar(categoria), limpiar(articulo));
    }

    private String limpiar(String texto) {
        return texto == null ? null : texto.trim();
    }

    /** Reporte (RF19): gastos del hogar agrupados por "tipo", "lugar" o "dia". */
    public List<Object[]> reporteAgrupado(String idHogar, String agrupar) {
        if (idHogar == null || idHogar.trim().isEmpty()) {
            System.out.println("[Servicio] Validacion: el ID del hogar es obligatorio para el reporte.");
            return List.of();
        }
        return gastoDAO.agruparGastos(idHogar.trim(), agrupar);
    }

    /** READ: acumulado en pesos del hogar. */
    public double calcularTotalGastoHogar(String idHogar) {
        if (idHogar == null || idHogar.trim().isEmpty()) {
            return 0.0;
        }
        return gastoDAO.obtenerTotalGastosHogar(idHogar);
    }

    /** UPDATE: valida y actualiza un gasto existente. */
    public boolean actualizarGasto(GastoDiario gasto) {
        String error = validar(gasto);
        if (error == null && (gasto.getIdGasto() == null || !idValido(gasto.getIdGasto()))) {
            error = "El ID del gasto es obligatorio y debe ser numerico.";
        }
        if (error != null) {
            System.out.println("[Servicio] Validacion: " + error);
            return false;
        }
        return gastoDAO.actualizarGasto(gasto);
    }

    /** DELETE: elimina un gasto por id. */
    public boolean eliminarGasto(String idGasto) {
        if (!idValido(idGasto)) {
            System.out.println("[Servicio] Validacion: el ID del gasto debe ser numerico.");
            return false;
        }
        return gastoDAO.eliminarGasto(idGasto.trim());
    }
}