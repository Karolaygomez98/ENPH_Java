package servicios;

import dao.IdentificacionDAO;

import java.math.BigDecimal;
import java.util.Map;

/** Capitulo A (RF01 a RF03) y valor total del mercado (RF09): validaciones antes de llamar al DAO. */
public class IdentificacionServicio {

    /** Resultados posibles del cuadernillo (RF02). Los codigos los controla Oracle (CHECK). */
    public static final String[] RESULTADOS = {
        "EC - Encuesta completa", "EI - Encuesta incompleta", "NH - Nadie en el hogar",
        "AT - Ausente temporal", "R - Rechazo", "V - Vacante",
        "OC - Ocupado", "O - Otro motivo"
    };

    private final IdentificacionDAO dao = new IdentificacionDAO();

    public Map<String, String> consultar(String idHogar) {
        return idHogar == null || idHogar.trim().isEmpty() ? null : dao.obtener(idHogar.trim());
    }

    /** Dias del periodo de 14 que ya estan habilitados para diligenciar (RF04): del 1 hasta el dia de hoy. */
    public int diasHabilitados(String idHogar) {
        Map<String, String> m = consultar(idHogar);
        if (m == null || m.get("dias") == null) {
            return 14;
        }
        return Math.max(1, Math.min(14, Integer.parseInt(m.get("dias"))));
    }

    private boolean cerrado(String idHogar) {
        Map<String, String> m = consultar(idHogar);
        return m != null && "Completado".equals(m.get("estado"));
    }

    /** Devuelve el mensaje de error, o null si se guardo. */
    public String guardarIdentificacion(String idHogar, int orden, String resultado) {
        if (idHogar == null || idHogar.trim().isEmpty()) {
            return "Seleccione un hogar.";
        }
        if (orden < 1 || orden > 99) {
            return "El numero de orden debe estar entre 1 y 99.";
        }
        if (resultado == null || resultado.trim().isEmpty()) {
            return "Seleccione el resultado del cuadernillo.";
        }
        if (cerrado(idHogar)) {
            return "El cuadernillo ya esta cerrado: no se puede modificar.";
        }
        String codigo = resultado.substring(0, resultado.indexOf(' '));
        return dao.guardarIdentificacion(idHogar.trim(), orden, codigo) ? null : "Oracle no guardo los datos.";
    }

    /** Devuelve el mensaje de error, o null si se guardo. Texto vacio borra el valor. */
    public String guardarValorMercado(String idHogar, String texto) {
        if (idHogar == null || idHogar.trim().isEmpty()) {
            return "Seleccione un hogar.";
        }
        BigDecimal valor = null;
        if (texto != null && !texto.trim().isEmpty()) {
            try {
                valor = new BigDecimal(texto.trim().replace(',', '.'));
            } catch (NumberFormatException e) {
                return "El valor del mercado debe ser un numero.";
            }
            if (valor.signum() < 0) {
                return "El valor del mercado no puede ser negativo.";
            }
        }
        if (cerrado(idHogar)) {
            return "El cuadernillo ya esta cerrado: no se puede modificar.";
        }
        return dao.guardarValorMercado(idHogar.trim(), valor) ? null : "Oracle no guardo el valor.";
    }
}
