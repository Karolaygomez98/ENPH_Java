package servicios;

import dao.ComidaPreparadaDAO;
import modelos.ComidaPreparada;

import java.util.ArrayList;
import java.util.List;

/** Logica de negocio de las comidas fuera del hogar: valida y luego llama al DAO. */
public class ComidaServicio {

    private final ComidaPreparadaDAO dao = new ComidaPreparadaDAO();

    /** Devuelve el mensaje de error, o null si la comida es valida. */
    private String validar(ComidaPreparada c) {
        if (c == null) return "La comida no puede ser nula.";
        if (c.getIdHogar() == null || c.getIdHogar().trim().isEmpty()) return "El hogar es obligatorio.";
        if (c.getDia() < 1 || c.getDia() > 14) return "El dia debe estar entre 1 y 14.";
        if (c.getEstablecimiento() == null || c.getEstablecimiento().trim().isEmpty()) return "La descripcion es obligatoria.";
        if ("Personal".equalsIgnoreCase(c.getCategoria()) && c.getDia() > GastoServicio.DIAS_GASTO_PERSONAL) {
            return "Los gastos personales solo se registran en los primeros " + GastoServicio.DIAS_GASTO_PERSONAL + " dias.";
        }
        if (c.getCantidad() < 1 || c.getCantidad() > 99) return "La cantidad debe estar entre 1 y 99.";
        if (c.getValor() <= 0) return "El valor pagado debe ser mayor a 0.";
        return null;
    }

    public String guardarNuevaComida(ComidaPreparada c) {
        String error = validar(c);
        if (error != null) return error;
        return dao.registrarComida(c) ? null : "Oracle no guardo la comida.";
    }

    public ComidaPreparada consultarComida(String id) {
        return id == null || id.trim().isEmpty() ? null : dao.obtenerPorId(id.trim());
    }

    public List<ComidaPreparada> consultarComidas(String idHogar) {
        return idHogar == null || idHogar.trim().isEmpty() ? new ArrayList<>() : dao.obtenerPorHogar(idHogar.trim());
    }

    public String actualizar(ComidaPreparada c) {
        String error = validar(c);
        if (error != null) return error;
        return dao.actualizarComida(c) ? null : "Oracle no actualizo la comida.";
    }

    public boolean eliminar(String id) {
        return id != null && !id.trim().isEmpty() && dao.eliminarComida(id.trim());
    }
}
