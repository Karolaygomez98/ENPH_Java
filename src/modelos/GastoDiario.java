package modelos;

public class GastoDiario {
    private String idGasto;
    private String idHogar;
    private String descripcionProducto;
    private double cantidad;
    private String unidadMedida;
    private double valorPagado;
    private String lugarCompra;

    public GastoDiario() {
    }

    public GastoDiario(String idGasto, String idHogar, String descripcionProducto, double cantidad, String unidadMedida, double valorPagado, String lugarCompra) {
        this.idGasto = idGasto;
        this.idHogar = idHogar;
        this.descripcionProducto = descripcionProducto;
        this.cantidad = cantidad;
        this.unidadMedida = unidadMedida;
        this.valorPagado = valorPagado;
        this.lugarCompra = lugarCompra;
    }

    public String getIdGasto() { return idGasto; }
    public void setIdGasto(String idGasto) { this.idGasto = idGasto; }

    public String getIdHogar() { return idHogar; }
    public void setIdHogar(String idHogar) { this.idHogar = idHogar; }

    public String getDescripcionProducto() { return descripcionProducto; }
    public void setDescripcionProducto(String descripcionProducto) { this.descripcionProducto = descripcionProducto; }

    public double getCantidad() { return cantidad; }
    public void setCantidad(double cantidad) { this.cantidad = cantidad; }

    public String getUnidadMedida() { return unidadMedida; }
    public void setUnidadMedida(String unidadMedida) { this.unidadMedida = unidadMedida; }

    public double getValorPagado() { return valorPagado; }
    public void setValorPagado(double valorPagado) { this.valorPagado = valorPagado; }

    public String getLugarCompra() { return lugarCompra; }
    public void setLugarCompra(String lugarCompra) { this.lugarCompra = lugarCompra; }
}