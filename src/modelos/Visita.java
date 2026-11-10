package modelos;

/** Visita de seguimiento a un hogar (capitulo B): fechas y horas en el formato de la interfaz. */
public class Visita {
    private String idVisita;
    private String idHogar;
    private String fecha;            // dd/mm/aaaa
    private String horaInicio;       // hh:mm
    private String horaFin;          // hh:mm
    private String resultado;
    private String partesPendientes; // puede ser vacio
    private String fechaProxima;     // dd/mm/aaaa o vacio
    private String horaProxima;      // hh:mm o vacio

    public Visita(String idVisita, String idHogar, String fecha, String horaInicio, String horaFin,
                  String resultado, String partesPendientes, String fechaProxima, String horaProxima) {
        this.idVisita = idVisita;
        this.idHogar = idHogar;
        this.fecha = fecha;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
        this.resultado = resultado;
        this.partesPendientes = partesPendientes;
        this.fechaProxima = fechaProxima;
        this.horaProxima = horaProxima;
    }

    public String getIdVisita() { return idVisita; }
    public void setIdVisita(String idVisita) { this.idVisita = idVisita; }
    public String getIdHogar() { return idHogar; }
    public String getFecha() { return fecha; }
    public String getHoraInicio() { return horaInicio; }
    public String getHoraFin() { return horaFin; }
    public String getResultado() { return resultado; }
    public String getPartesPendientes() { return partesPendientes; }
    public String getFechaProxima() { return fechaProxima; }
    public String getHoraProxima() { return horaProxima; }

    @Override
    public String toString() {
        return "Visita #" + idVisita + " | hogar " + idHogar + " | " + fecha + " " + horaInicio + "-" + horaFin
                + " | " + resultado + (partesPendientes == null || partesPendientes.isEmpty() ? "" : " | pendiente: " + partesPendientes);
    }
}
