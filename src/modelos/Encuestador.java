package modelos;

public class Encuestador {
    private String usuario;
    private String nombreCompleto;
    private String rol;

    public Encuestador() {
    }

    public Encuestador(String usuario, String nombreCompleto, String rol) {
        this.usuario = usuario;
        this.nombreCompleto = nombreCompleto;
        this.rol = rol;
    }

    public String getUsuario() { return usuario; }
    public void setUsuario(String usuario) { this.usuario = usuario; }

    public String getNombreCompleto() { return nombreCompleto; }
    public void setNombreCompleto(String nombreCompleto) { this.nombreCompleto = nombreCompleto; }

    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }
}