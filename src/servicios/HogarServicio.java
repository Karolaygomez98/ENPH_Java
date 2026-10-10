package servicios;

import dao.HogarDAO;
import modelos.Hogar;

import java.util.List;

/** Logica de negocio para hogares: valida y luego llama al DAO. */
public class HogarServicio {

    private final HogarDAO hogarDAO = new HogarDAO();

    private boolean vacio(String s) {
        return s == null || s.trim().isEmpty();
    }

    private String validar(Hogar h) {
        if (h == null) return "El hogar no puede ser nulo.";
        if (vacio(h.getIdHogar())) return "El ID del hogar es obligatorio.";
        if (vacio(h.getCodigoUbicacion())) return "El codigo de ubicacion es obligatorio.";
        if (vacio(h.getJefeHogar())) return "El jefe de hogar es obligatorio.";
        if (vacio(h.getDireccion())) return "La direccion es obligatoria.";
        return null;
    }

    public boolean guardarNuevoHogar(Hogar h) {
        String error = validar(h);
        if (error != null) {
            System.out.println("[Servicio] Validacion: " + error);
            return false;
        }
        return hogarDAO.registrarHogar(h);
    }

    public Hogar consultarHogar(String idHogar) {
        if (vacio(idHogar)) {
            System.out.println("[Servicio] Validacion: el ID del hogar es obligatorio.");
            return null;
        }
        return hogarDAO.obtenerHogarPorId(idHogar.trim());
    }

    public List<Hogar> listarHogares() {
        return hogarDAO.obtenerTodosLosHogares();
    }

    public boolean actualizarHogar(Hogar h) {
        String error = validar(h);
        if (error != null) {
            System.out.println("[Servicio] Validacion: " + error);
            return false;
        }
        return hogarDAO.actualizarHogar(h);
    }

    public boolean eliminarHogar(String idHogar) {
        if (vacio(idHogar)) {
            System.out.println("[Servicio] Validacion: el ID del hogar es obligatorio.");
            return false;
        }
        return hogarDAO.eliminarHogar(idHogar.trim());
    }
}