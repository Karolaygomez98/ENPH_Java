package servicios;

import dao.CaracteristicaDAO;
import modelos.Caracteristica;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/** Logica de negocio del capitulo C: valida y luego llama al DAO. */
public class CaracteristicaServicio {

    private final CaracteristicaDAO dao = new CaracteristicaDAO();
    private static final List<String> FRECUENCIAS = Arrays.asList("1", "2", "2.1", "3", "4", "5", "6", "7");

    /** Devuelve el mensaje de error, o null si la respuesta es valida. */
    private String validar(Caracteristica c) {
        if (c == null) return "La respuesta no puede ser nula.";
        if (c.getIdHogar() == null || c.getIdHogar().trim().isEmpty()) return "El hogar es obligatorio.";
        if (c.getIdGrupo() == null || !c.getIdGrupo().matches("\\d{2}")) return "El grupo de alimentos no es valido.";
        if (!FRECUENCIAS.contains(c.getFrecuencia())) return "La frecuencia de compra no es valida.";
        if (c.getValorUltimaCompra() != null && c.getValorUltimaCompra() < 0) return "El valor de la ultima compra no puede ser negativo.";
        if (c.getComprara() != null && !c.getComprara().isEmpty() && !c.getComprara().matches("[SN]")) return "Comprara debe ser S o N.";
        return null;
    }

    public String guardar(Caracteristica c) {
        String error = validar(c);
        if (error != null) {
            System.out.println("[Servicio] Validacion: " + error);
            return error;
        }
        return dao.guardar(c) ? null : "Oracle no guardo la respuesta.";
    }

    public boolean eliminar(String idHogar, String idGrupo) {
        return dao.eliminar(idHogar, idGrupo);
    }

    public List<Caracteristica> consultar(String idHogar) {
        return idHogar == null || idHogar.trim().isEmpty() ? new ArrayList<>() : dao.obtenerPorHogar(idHogar.trim());
    }
}
