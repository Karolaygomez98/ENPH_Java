import conexion.ConexionBD;
import modelos.GastoDiario;
import modelos.Hogar;
import servicios.GastoServicio;
import servicios.HogarServicio;

import java.util.List;

/**
 * Prueba del backend: conexion a Oracle y CRUD completo de gasto_diario y hogares.
 * Requiere que exista el hogar HOG-68001-014 en la tabla hogares.
 */
public class Main {

    private static final String HOGAR = "HOG-68001-014";

    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println("   DANE - ENPH / PRUEBA CRUD BACKEND (CUADERNILLO 2)");
        System.out.println("==================================================");

        System.out.println("\n[0] Probando conexion con Oracle...");
        if (!ConexionBD.probarConexion()) {
            System.out.println("    No hay conexion. Revise usuario, clave y que Oracle este activo.");
            return;
        }
        System.out.println("    Conectado a Oracle");

        GastoServicio servicio = new GastoServicio();

        // ---------- CREATE ----------
        System.out.println("\n[1] CREATE - registrar un gasto nuevo");
        GastoDiario gasto = new GastoDiario(null, HOGAR, 2, "Alimentos", "Leche entera", 3.0, "Litros", 12000.0, "Supermercado");
        if (!servicio.guardarNuevoGasto(gasto)) {
            System.out.println("    No se pudo crear el gasto. Fin de la prueba.");
            return;
        }
        System.out.println("    Creado: " + gasto);

        // ---------- READ ----------
        System.out.println("\n[2] READ - consultar el gasto por ID y los gastos del hogar");
        GastoDiario leido = servicio.consultarGasto(gasto.getIdGasto());
        System.out.println("    Por ID: " + leido);
        List<GastoDiario> lista = servicio.consultarGastosHogar(HOGAR);
        System.out.println("    Gastos del hogar " + HOGAR + " (" + lista.size() + "):");
        for (GastoDiario g : lista) {
            System.out.println("      - " + g);
        }
        System.out.println("    Total gastado por el hogar: $" + servicio.calcularTotalGastoHogar(HOGAR));

        // ---------- UPDATE ----------
        System.out.println("\n[3] UPDATE - cambiar cantidad y valor pagado");
        gasto.setCantidad(5.0);
        gasto.setValorPagado(20000.0);
        if (servicio.actualizarGasto(gasto)) {
            System.out.println("    Despues de actualizar: " + servicio.consultarGasto(gasto.getIdGasto()));
        } else {
            System.out.println("    No se pudo actualizar.");
        }

        // ---------- DELETE ----------
        System.out.println("\n[4] DELETE - eliminar el gasto de prueba");
        if (servicio.eliminarGasto(gasto.getIdGasto())) {
            System.out.println("    Eliminado. Consulta posterior: " + servicio.consultarGasto(gasto.getIdGasto()));
        } else {
            System.out.println("    No se pudo eliminar.");
        }

        // =====================================================
        //  CRUD DE LA TABLA hogares
        // =====================================================
        System.out.println("\n--------------------------------------------------");
        System.out.println("   CRUD TABLA HOGARES");
        System.out.println("--------------------------------------------------");
        HogarServicio hogares = new HogarServicio();
        Hogar nuevo = new Hogar("HOG-PRUEBA-001", "68001", "Carlos Perez", "Carrera 10 No 20-30", "En proceso");

        System.out.println("\n[H1] CREATE - registrar un hogar nuevo");
        if (!hogares.guardarNuevoHogar(nuevo)) {
            System.out.println("    No se pudo crear el hogar. Fin de la prueba.");
            return;
        }
        System.out.println("    Creado: " + nuevo);

        System.out.println("\n[H2] READ - consultar por ID y listar los hogares");
        System.out.println("    Por ID: " + hogares.consultarHogar(nuevo.getIdHogar()));
        List<Hogar> todos = hogares.listarHogares();
        System.out.println("    Hogares registrados (" + todos.size() + "):");
        for (Hogar h : todos) {
            System.out.println("      - " + h);
        }

        System.out.println("\n[H3] UPDATE - cambiar el estado y la direccion");
        nuevo.setEstado("Completado");
        nuevo.setDireccion("Carrera 10 No 22-40");
        if (hogares.actualizarHogar(nuevo)) {
            System.out.println("    Despues de actualizar: " + hogares.consultarHogar(nuevo.getIdHogar()));
        } else {
            System.out.println("    No se pudo actualizar.");
        }

        System.out.println("\n[H4] DELETE - eliminar el hogar de prueba");
        if (hogares.eliminarHogar(nuevo.getIdHogar())) {
            System.out.println("    Eliminado. Consulta posterior: " + hogares.consultarHogar(nuevo.getIdHogar()));
        } else {
            System.out.println("    No se pudo eliminar.");
        }

        System.out.println("\n==================================================");
        System.out.println("   FIN DE LA PRUEBA CRUD");
        System.out.println("==================================================");
    }
}