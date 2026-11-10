package servicios;

import dao.ValidacionDAO;

import java.util.List;

/** Validacion de consistencia (RF21): delega en el procedimiento almacenado de Oracle. */
public class ValidacionServicio {

    private final ValidacionDAO dao = new ValidacionDAO();

    /** Inconsistencias del hogar, o null si el hogar es vacio o Oracle no respondio. */
    public List<String[]> validar(String idHogar) {
        if (idHogar == null || idHogar.trim().isEmpty()) {
            return null;
        }
        return dao.validarHogar(idHogar.trim());
    }
}
