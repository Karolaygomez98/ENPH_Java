package modelos;

public class Hogar {
    private String idHogar;
    private String codigoUbicacion;
    private String jefeHogar;
    private String direccion;
    private String estado;

    public Hogar() {
    }

    public Hogar(String idHogar, String codigoUbicacion, String jefeHogar, String direccion, String estado) {
        this.idHogar = idHogar;
        this.codigoUbicacion = codigoUbicacion;
        this.jefeHogar = jefeHogar;
        this.direccion = direccion;
        this.estado = estado;
    }

    public String getIdHogar() { return idHogar; }
    public void setIdHogar(String idHogar) { this.idHogar = idHogar; }

    public String getCodigoUbicacion() { return codigoUbicacion; }
    public void setCodigoUbicacion(String codigoUbicacion) { this.codigoUbicacion = codigoUbicacion; }

    public String getJefeHogar() { return jefeHogar; }
    public void setJefeHogar(String jefeHogar) { this.jefeHogar = jefeHogar; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}