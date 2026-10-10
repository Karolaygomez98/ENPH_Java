package modelos;

/**
 * Gasto diario registrado en el Cuadernillo 2 (capitulo D) para un hogar.
 * dia: 1 a 14 del periodo de recoleccion. categoria: opcional (Alimentos, Transporte, etc.).
 */
public class GastoDiario {
    private String idGasto;
    private String idHogar;
    private int dia = 1;
    private String categoria;
    private String descripcionProducto;
    private double cantidad;
    private String unidadMedida;
    private double valorPagado;
    private String lugarCompra;

    public GastoDiario() {
    }

    /** Constructor corto (dia = 1, sin categoria): se mantiene por compatibilidad. */
    public GastoDiario(String idGasto, String idHogar, String descripcionProducto, double cantidad,
                       String unidadMedida, double valorPagado, String lugarCompra) {
        this(idGasto, idHogar, 1, null, descripcionProducto, cantidad, unidadMedida, valorPagado, lugarCompra);
    }

    public GastoDiario(String idGasto, String idHogar, int dia, String categoria, String descripcionProducto,
                       double cantidad, String unidadMedida, double valorPagado, String lugarCompra) {
        this.idGasto = idGasto;
        this.idHogar = idHogar;
        this.dia = dia;
        this.categoria = categoria;
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

    public int getDia() { return dia; }
    public void setDia(int dia) { this.dia = dia; }

    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }

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

    @Override
    public String toString() {
        return "Gasto #" + idGasto + " | hogar " + idHogar + " | dia " + dia
                + " | " + (categoria == null ? "-" : categoria)
                + " | " + descripcionProducto + " | " + cantidad + " " + unidadMedida
                + " | $" + valorPagado + " | " + lugarCompra;
    }
}