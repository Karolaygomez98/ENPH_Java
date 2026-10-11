package servicios;

import dao.EncuestadorDAO;
import modelos.Encuestador;

import java.util.List;

/** Logica de negocio para encuestadores: valida y luego llama al DAO. */
public class EncuestadorServicio {

    private final EncuestadorDAO encuestadorDAO = new EncuestadorDAO();

    private boolean vacio(String s) {
        return s == null || s.trim().isEmpty();
    }

    private String validar(Encuestador e) {
        if (e == null) return "El encuestador no puede ser nulo.";
        if (vacio(e.getUsuario())) return "El usuario es obligatorio.";
        if (vacio(e.getNombreCompleto())) return "El nombre completo es obligatorio.";
        if (vacio(e.getRol())) return "El rol es obligatorio.";
        return null;
    }

    public boolean guardarNuevoEncuestador(Encuestador e) {
        String error = validar(e);
        if (error != null) {
            System.out.println("[Servicio] Validacion: " + error);
            return false;
        }
        return encuestadorDAO.registrarEncuestador(e);
    }

    public Encuestador consultarEncuestador(String usuario) {
        if (vacio(usuario)) {
            System.out.println("[Servicio] Validacion: el usuario es obligatorio.");
            return null;
        }
        return encuestadorDAO.obtenerEncuestadorPorUsuario(usuario.trim());
    }

    public List<Encuestador> listarEncuestadores() {
        return encuestadorDAO.obtenerTodosLosEncuestadores();
    }

    public boolean actualizarEncuestador(Encuestador e) {
        String error = validar(e);
        if (error != null) {
            System.out.println("[Servicio] Validacion: " + error);
            return false;
        }
        return encuestadorDAO.actualizarEncuestador(e);
    }

    public boolean eliminarEncuestador(String usuario) {
        if (vacio(usuario)) {
            System.out.println("[Servicio] Validacion: el usuario es obligatorio.");
            return false;
        }
        return encuestadorDAO.eliminarEncuestador(usuario.trim());
    }
}