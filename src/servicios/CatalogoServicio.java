package servicios;

import dao.CatalogoDAO;

import java.util.ArrayList;
import java.util.List;

/**
 * RF17: entrega las listas de valores validos de cada catalogo. Si Oracle no responde
 * se usan los valores del cuadernillo como respaldo para que la pantalla no quede vacia.
 */
public class CatalogoServicio {

    private static final CatalogoDAO DAO = new CatalogoDAO();

    private static String[] cargar(String catalogo, String[] respaldo) {
        List<String> lista = DAO.nombres(catalogo);
        return lista.isEmpty() ? respaldo : lista.toArray(new String[0]);
    }

    public static String[] unidades() {
        return cargar("unidad", new String[]{"Unidad", "Kilos", "Gramos", "Libras", "Litros", "Mililitros",
            "Paquete", "Docena", "Servicio"});
    }

    public static String[] formasAdquisicion() {
        return cargar("forma", new String[]{"Comprado", "Producido en el hogar", "Obsequio", "Pago en especie", "Otra"});
    }

    public static String[] lugares() {
        return cargar("lugar", new String[]{"Tienda de barrio", "Supermercado", "Plaza de mercado",
            "Vendedor ambulante", "Panadería", "Droguería", "No especificado"});
    }

    /** Frecuencias de compra; la primera opcion, "No aplica", deja el dato en blanco. */
    public static String[] frecuencias() {
        String[] base = cargar("frecuencia", new String[]{"Nunca", "Diario", "Varias veces por semana", "Semanal",
            "Quincenal", "Mensual", "Bimestral", "Trimestral / Esporádico"});
        List<String> l = new ArrayList<>();
        l.add("No aplica");
        for (String s : base) {
            l.add(s);
        }
        return l.toArray(new String[0]);
    }

    public static String[] tiposComida() {
        return cargar("comida", new String[]{"Desayuno", "Almuerzo", "Comida / Cena", "Refrigerio / Bebidas"});
    }
}
