package servicios;

import dao.GastoDiarioDAO;
import modelos.GastoDiario;
import java.util.List;

public class GastoServicio {

    private final GastoDiarioDAO gastoDAO;

    public GastoServicio() {
        this.gastoDAO = new GastoDiarioDAO();
    }

    /**
     * Valida la lógica de negocio antes de registrar un gasto
     */
    public boolean guardarNuevoGasto(GastoDiario gasto) {
        // Reglas de negocio (POO)
        if (gasto == null) {
            System.out.println("⚠️️ [Servicio] El objeto gasto no puede ser nulo.");
            return false;
        }

        if (gasto.getDescripcionProducto() == null || gasto.getDescripcionProducto().trim().isEmpty()) {
            System.out.println("⚠️ [Servicio] La descripción del producto es obligatoria.");
            return false;
        }

        if (gasto.getCantidad() <= 0) {
            System.out.println("⚠️ [Servicio] La cantidad debe ser mayor a 0.");
            return false;
        }

        if (gasto.getValorPagado() <= 0) {
            System.out.println("⚠️ [Servicio] El valor pagado debe ser mayor a 0.");
            return false;
        }

        // Si pasa todas las validaciones, se envía al DAO
        return gastoDAO.registrarGasto(gasto);
    }

    /**
     * Obtiene todos los gastos registrados para un hogar específico
     */
    public List<GastoDiario> consultarGastosHogar(String idHogar) {
        if (idHogar == null || idHogar.trim().isEmpty()) {
            System.out.println("⚠️ [Servicio] El ID del hogar es obligatorio para consultar.");
            return List.of();
        }
        return gastoDAO.obtenerGastosPorHogar(idHogar);
    }

    /**
     * Obtiene el acumulado total del gasto en dinero ($) del hogar
     */
    public double calcularTotalGastoHogar(String idHogar) {
        if (idHogar == null || idHogar.trim().isEmpty()) {
            return 0.0;
        }
        return gastoDAO.obtenerTotalGastosHogar(idHogar);
    }
}