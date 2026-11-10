package modelos;

/** Capitulo C: frecuencia de compra de un grupo de alimentos en un hogar. */
public class Caracteristica {
    private String idHogar;
    private String idGrupo;        // 01 a 25
    private String frecuencia;     // 1, 2, 2.1, 3, 4, 5, 6 o 7
    private Double valorUltimaCompra;   // puede ser nulo
    private String comprara;       // S, N o vacio

    public Caracteristica(String idHogar, String idGrupo, String frecuencia, Double valorUltimaCompra, String comprara) {
        this.idHogar = idHogar;
        this.idGrupo = idGrupo;
        this.frecuencia = frecuencia;
        this.valorUltimaCompra = valorUltimaCompra;
        this.comprara = comprara;
    }

    public String getIdHogar() { return idHogar; }
    public String getIdGrupo() { return idGrupo; }
    public String getFrecuencia() { return frecuencia; }
    public Double getValorUltimaCompra() { return valorUltimaCompra; }
    public String getComprara() { return comprara; }

    @Override
    public String toString() {
        return "Grupo " + idGrupo + " | hogar " + idHogar + " | frecuencia " + frecuencia
                + " | ultima compra " + valorUltimaCompra + " | comprara " + comprara;
    }
}
