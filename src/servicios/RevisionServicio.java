package servicios;

import dao.RevisionDAO;

import java.util.List;
import java.util.Map;

/** Revision del supervisor: lista los casos, muestra el resumen y finaliza o devuelve el cuadernillo. */
public class RevisionServicio {

    private final RevisionDAO dao = new RevisionDAO();

    public List<String[]> disponibles() {
        return dao.disponibles();
    }

    public Map<String, String> resumen(String idHogar) {
        return idHogar == null || idHogar.trim().isEmpty() ? null : dao.resumen(idHogar.trim());
    }

    /** accion: F = finalizar la revision, D = devolver para correccion. */
    public String[] revisar(String idHogar, String accion, String idUsuario) {
        if (idHogar == null || idHogar.trim().isEmpty()) {
            return new String[] { "0", "Seleccione un hogar." };
        }
        if (accion == null || !accion.matches("[FD]")) {
            return new String[] { "0", "Accion no valida." };
        }
        if (idUsuario == null || idUsuario.trim().isEmpty()) {
            return new String[] { "0", "No se identifico al usuario." };
        }
        String[] r = dao.revisar(idHogar.trim(), accion, idUsuario.trim());
        return r != null ? r : new String[] { "0", "No se pudo revisar en Oracle. Revise la conexion y que exista sp_revisar_cuadernillo." };
    }
}
