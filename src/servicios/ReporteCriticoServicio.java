package servicios;

import dao.ReporteCriticoDAO;

import java.util.ArrayList;
import java.util.List;

/** Reportes del Critico-Digitador: solo ese rol puede reportar; los demas roles los consultan. */
public class ReporteCriticoServicio {

    private final ReporteCriticoDAO dao = new ReporteCriticoDAO();

    /** Guarda las inconsistencias como reporte. Devuelve un mensaje para mostrar en pantalla. */
    public String reportar(String idHogar, String idUsuario, String rol, List<String[]> hallazgos) {
        if (!"Critico-Digitador".equals(rol)) {
            return "Solo el Critico-Digitador puede reportar inconsistencias.";
        }
        if (idHogar == null || idHogar.trim().isEmpty() || idUsuario == null || idUsuario.trim().isEmpty()) {
            return "Faltan datos del hogar o del usuario.";
        }
        if (hallazgos == null || hallazgos.isEmpty()) {
            return "No hay inconsistencias para reportar. Valide el hogar primero.";
        }
        int n = dao.guardar(idHogar.trim(), idUsuario.trim(), hallazgos);
        if (n < 0) {
            return "No se pudo guardar el reporte. Revise la conexion con Oracle.";
        }
        if (n == 0) {
            return "Esas inconsistencias ya estaban reportadas hoy.";
        }
        return n + " inconsistencia(s) reportada(s) al encuestador.";
    }

    public List<String[]> consultar(String idHogar) {
        if (idHogar == null || idHogar.trim().isEmpty()) {
            return new ArrayList<>();
        }
        List<String[]> lista = dao.listar(idHogar.trim());
        return lista != null ? lista : new ArrayList<>();
    }
}
