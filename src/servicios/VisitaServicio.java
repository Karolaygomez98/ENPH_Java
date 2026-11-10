package servicios;

import dao.VisitaDAO;
import modelos.Visita;

import java.util.List;

/** Logica de negocio para las visitas: valida y luego llama al DAO. */
public class VisitaServicio {

    private final VisitaDAO visitaDAO = new VisitaDAO();

    private static final String FECHA = "\\d{2}/\\d{2}/\\d{4}";
    private static final String HORA = "([01]\\d|2[0-3]):[0-5]\\d";
    /** RF05: maximo de visitas de seguimiento por hogar (periodo de 14 dias). */
    private static final int MAX_VISITAS = 14;

    private boolean vacio(String s) {
        return s == null || s.trim().isEmpty();
    }

    /** Devuelve el mensaje de error, o null si la visita es valida. */
    private String validar(Visita v) {
        if (v == null) return "La visita no puede ser nula.";
        if (vacio(v.getIdHogar())) return "El hogar es obligatorio.";
        if (vacio(v.getFecha()) || !v.getFecha().matches(FECHA)) return "La fecha debe tener el formato dd/mm/aaaa.";
        if (vacio(v.getHoraInicio()) || !v.getHoraInicio().matches(HORA)) return "La hora de inicio debe ser hh:mm.";
        if (vacio(v.getHoraFin()) || !v.getHoraFin().matches(HORA)) return "La hora de fin debe ser hh:mm.";
        if (v.getHoraFin().compareTo(v.getHoraInicio()) <= 0) return "La hora de fin debe ser posterior a la de inicio.";
        if (vacio(v.getResultado())) return "El resultado es obligatorio.";
        if (!vacio(v.getFechaProxima()) && !v.getFechaProxima().matches(FECHA)) return "La fecha de la proxima visita no es valida.";
        if (!vacio(v.getHoraProxima()) && !v.getHoraProxima().matches(HORA)) return "La hora de la proxima visita no es valida.";
        return null;
    }

    public String guardarNuevaVisita(Visita v) {
        String error = validar(v);
        if (error != null) {
            System.out.println("[Servicio] Validacion: " + error);
            return error;
        }
        if (visitaDAO.obtenerVisitasPorHogar(v.getIdHogar().trim()).size() >= MAX_VISITAS) {
            return "El hogar ya tiene las " + MAX_VISITAS + " visitas permitidas.";
        }
        return visitaDAO.registrarVisita(v) ? null : "Oracle no guardo la visita.";
    }

    public List<Visita> consultarVisitas(String idHogar) {
        return vacio(idHogar) ? new java.util.ArrayList<>() : visitaDAO.obtenerVisitasPorHogar(idHogar.trim());
    }
}
