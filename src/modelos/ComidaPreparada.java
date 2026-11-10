package modelos;

/** Comida preparada fuera del hogar (capitulo D1) consumida en un dia de recoleccion. */
public class ComidaPreparada {
    private String idComida;
    private String idHogar;
    private int dia;
    private String tipo;            // Desayuno, Almuerzo, Cena, Refrigerio
    private String establecimiento; // se guarda en la columna descripcion
    private double valor;
    private int cantidad = 1;
    private boolean domicilio = false;
    private String categoria = "Personal";      // Hogar / Personal
    private String lugarCompra = "No especificado";
    private String formaAdquisicion = "Comprado";
    private String frecuenciaCompra;             // null = no aplica

    public ComidaPreparada(String idComida, String idHogar, int dia, String tipo, String establecimiento, double valor) {
        this.idComida = idComida;
        this.idHogar = idHogar;
        this.dia = dia;
        this.tipo = tipo;
        this.establecimiento = establecimiento;
        this.valor = valor;
    }

    public String getIdComida() { return idComida; }
    public void setIdComida(String idComida) { this.idComida = idComida; }
    public String getIdHogar() { return idHogar; }
    public int getDia() { return dia; }
    public void setDia(int dia) { this.dia = dia; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public String getEstablecimiento() { return establecimiento; }
    public void setEstablecimiento(String establecimiento) { this.establecimiento = establecimiento; }
    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }
    public boolean isDomicilio() { return domicilio; }
    public void setDomicilio(boolean domicilio) { this.domicilio = domicilio; }
    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }
    public String getLugarCompra() { return lugarCompra; }
    public void setLugarCompra(String lugarCompra) { this.lugarCompra = lugarCompra; }
    public String getFormaAdquisicion() { return formaAdquisicion; }
    public void setFormaAdquisicion(String formaAdquisicion) { this.formaAdquisicion = formaAdquisicion; }
    public String getFrecuenciaCompra() { return frecuenciaCompra; }
    public void setFrecuenciaCompra(String frecuenciaCompra) { this.frecuenciaCompra = frecuenciaCompra; }
    public double getValor() { return valor; }
    public void setValor(double valor) { this.valor = valor; }

    @Override
    public String toString() {
        return "Comida #" + idComida + " | hogar " + idHogar + " | dia " + dia + " | " + tipo + " | "
                + establecimiento + " | $" + valor;
    }
}
