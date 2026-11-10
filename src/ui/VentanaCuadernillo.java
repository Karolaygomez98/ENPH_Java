package ui;

import javax.swing.*;
import java.awt.*;

/**
 * Cuadernillo 2 de un hogar. Cada capitulo es una pestana (JTabbedPane).
 * El rol del usuario se usa para decidir que puede hacer (por ejemplo, solo el Encuestador cierra).
 */
public class VentanaCuadernillo extends JFrame {

    public VentanaCuadernillo(String idHogar, String jefeHogar, String rol, String idUsuario) {
        setTitle("Cuadernillo 2 - Hogar " + idHogar);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE); // cierra solo esta ventana, no la aplicacion
        setSize(900, 680);
        setLocationRelativeTo(null);

        JLabel encabezado = new JLabel("Hogar " + idHogar + "   |   Jefe de hogar: " + jefeHogar);
        encabezado.setFont(encabezado.getFont().deriveFont(Font.BOLD, 13f));
        encabezado.setBorder(BorderFactory.createEmptyBorder(10, 12, 4, 12));
        JPanel norte = new JPanel(new BorderLayout());
        norte.add(Estilo.encabezado("Cuadernillo 2", "Cuadernillo del hogar"), BorderLayout.NORTH);
        norte.add(encabezado, BorderLayout.SOUTH);
        add(norte, BorderLayout.NORTH);

        JTabbedPane pestanas = new JTabbedPane();
        // Solo el Encuestador diligencia el cuadernillo; Supervisor y Critico-Digitador lo consultan (solo lectura)
        JPanel identificacion = new PanelIdentificacion(idHogar);
        JPanel visitas = new PanelVisitas(idHogar);
        JPanel caracteristicas = new PanelCaracteristicas(idHogar);
        JPanel gastos = new PanelGastos(idHogar);
        JPanel comidas = new PanelComidas(idHogar);
        if (!"Encuestador".equals(rol)) {
            Estilo.soloLectura(identificacion);
            Estilo.soloLectura(visitas);
            Estilo.soloLectura(caracteristicas);
            Estilo.soloLectura(gastos);
            Estilo.soloLectura(comidas);
        }
        pestanas.addTab("Identificacion (cap. A)", identificacion);
        pestanas.addTab("Visitas", visitas);
        pestanas.addTab("Caracteristicas (cap. C)", caracteristicas);
        pestanas.addTab("Gastos diarios", gastos);
        pestanas.addTab("Comidas fuera del hogar", comidas);
        pestanas.addTab("Reportes", new PanelReportes(idHogar));
        pestanas.addTab("Cierre y validacion", new PanelCierre(idHogar, rol, idUsuario));
        add(pestanas, BorderLayout.CENTER);
    }
}
