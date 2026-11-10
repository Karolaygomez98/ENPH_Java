package servicios;

import dao.CierreDAO;

import java.util.Arrays;
import java.util.List;

/** Cierre del cuadernillo: valida los datos de entrada y delega las reglas en Oracle. */
public class CierreServicio {

    private final CierreDAO dao = new CierreDAO();
    private static final List<String> FORMAS_PAGO = Arrays.asList("EFE", "TDB", "TCR", "FIA", "BON", "OTR");

    /** Devuelve {ok, mensaje}; ok = "1" si el cuadernillo quedo cerrado. */
    public String[] cerrar(String idHogar, String fiesta, String formaPago, String idUsuario) {
        if (idHogar == null || idHogar.trim().isEmpty()) {
            return new String[] { "0", "Seleccione un hogar." };
        }
        if (fiesta == null || !fiesta.matches("[SN]")) {
            return new String[] { "0", "Indique si hubo gastos por fiestas o reuniones." };
        }
        if (formaPago == null || !FORMAS_PAGO.contains(formaPago)) {
            return new String[] { "0", "Seleccione la principal forma de pago." };
        }
        if (idUsuario == null || idUsuario.trim().isEmpty()) {
            return new String[] { "0", "No se identifico al usuario." };
        }
        String[] r = dao.cerrar(idHogar.trim(), fiesta, formaPago, idUsuario.trim());
        return r != null ? r : new String[] { "0", "No se pudo cerrar en Oracle. Revise la conexion y que exista sp_cerrar_cuadernillo." };
    }
}
