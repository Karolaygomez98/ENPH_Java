import modelos.GastoDiario;
import servicios.GastoServicio;

public class Main {
    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println("   DANE - ENPH / PRUEBA BACKEND JAVA (CUADERNILLO 2)   ");
        System.out.println("==================================================");

        // 1. Instanciar el servicio de negocio (Capa de Servicios)
        GastoServicio servicio = new GastoServicio();

        // 2. Crear un objeto de modelo GastoDiario (POO)
        System.out.println("\n[1] Creando instancia de GastoDiario...");
        GastoDiario nuevoGasto = new GastoDiario(
            "1", 
            "HOG-68001-014", 
            "Arroz blanco 1Kg", 
            2.0, 
            "Kilos", 
            8400.0, 
            "Tienda de barrio"
        );

        // Muestra de atributos usando getters
        System.out.println("    - Hogar ID: " + nuevoGasto.getIdHogar());
        System.out.println("    - Producto: " + nuevoGasto.getDescripcionProducto());
        System.out.println("    - Cantidad: " + nuevoGasto.getCantidad() + " " + nuevoGasto.getUnidadMedida());
        System.out.println("    - Valor Pagado: $" + nuevoGasto.getValorPagado());

        // 3. Validar y procesar con la regla de negocio
        System.out.println("\n[2] Procesando registro mediante GastoServicio...");
        boolean resultado = servicio.guardarNuevoGasto(nuevoGasto);

        if (resultado) {
            System.out.println("\n[ÉXITO] El gasto se validó correctamente en el Backend Java.");
        } else {
            System.out.println("\n[INFO] Ejecutado (Pendiente conexión activa a Base de Datos PostgreSQL).");
        }

        System.out.println("==================================================");
    }
}