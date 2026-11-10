package ui;

import servicios.CierreServicio;
import servicios.ReporteCriticoServicio;
import servicios.ValidacionServicio;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Pestana "Cierre y validacion".
 * Validar (RF21): ejecuta sp_validar_hogar en Oracle y lista las inconsistencias; si no hay filas, el hogar es consistente.
 * Cerrar (RF15 y RF16): ejecuta sp_cerrar_cuadernillo; Oracle revisa que la informacion este completa y deja el
 * cuadernillo en estado Completado. Solo el Encuestador puede cerrar.
 */
public class PanelCierre extends JPanel {

    private static final String[] FORMAS_PAGO = {
        "EFE - Efectivo", "TDB - Tarjeta d\u00e9bito", "TCR - Tarjeta cr\u00e9dito",
        "FIA - Fiado", "BON - Bonos", "OTR - Otra"
    };

    private final String idHogar;
    private final ValidacionServicio validacion = new ValidacionServicio();
    private final CierreServicio cierre = new CierreServicio();
    private final ReporteCriticoServicio reportes = new ReporteCriticoServicio();
    private final String idUsuario;
    private final String rol;
    private List<String[]> ultimosHallazgos = new java.util.ArrayList<>();

    private final DefaultTableModel modelo = new DefaultTableModel(new String[]{"Regla", "Detalle"}, 0) {
        @Override
        public boolean isCellEditable(int f, int c) {
            return false;
        }
    };
    private final JTable tabla = new JTable(modelo);
    private final JLabel lblValidacion = new JLabel(" ");
    private final JButton btnReportar = new JButton("Reportar inconsistencias");
    private final DefaultTableModel modeloReportes =
            new DefaultTableModel(new String[]{"Fecha", "Reportado por", "Regla", "Detalle"}, 0) {
        @Override
        public boolean isCellEditable(int f, int c) {
            return false;
        }
    };
    private final JTable tablaReportes = new JTable(modeloReportes);

    private final JComboBox<String> cmbFiesta = new JComboBox<>(new String[]{"No", "S\u00ed"});
    private final JComboBox<String> cmbPago = new JComboBox<>(FORMAS_PAGO);
    private final JButton btnValidar = new JButton("Validar hogar");
    private final JButton btnCerrar = new JButton("Cerrar cuadernillo");
    private final JLabel lblCierre = new JLabel(" ");

    public PanelCierre(String idHogar, String rol, String idUsuario) {
        this.idHogar = idHogar;
        this.rol = rol;
        this.idUsuario = idUsuario;
        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        // Arriba: validacion
        JPanel arriba = new JPanel(new BorderLayout(6, 6));
        arriba.setBorder(BorderFactory.createTitledBorder("1. Validar la consistencia de los datos"));
        JPanel barra = new JPanel(new FlowLayout(FlowLayout.LEFT));
        barra.add(btnValidar);
        barra.add(btnReportar);
        barra.add(lblValidacion);
        arriba.add(barra, BorderLayout.NORTH);
        Estilo.estilizarTabla(tabla);
        tabla.getColumnModel().getColumn(0).setMaxWidth(120);
        arriba.add(new JScrollPane(tabla), BorderLayout.CENTER);

        // Reportes que el Critico-Digitador dejo para este hogar (los ven todos los roles)
        JPanel medio = new JPanel(new BorderLayout(6, 6));
        medio.setBorder(BorderFactory.createTitledBorder("Reportes del cr\u00edtico-digitador"));
        Estilo.estilizarTabla(tablaReportes);
        tablaReportes.getColumnModel().getColumn(0).setMaxWidth(130);
        tablaReportes.getColumnModel().getColumn(2).setMaxWidth(110);
        medio.add(new JScrollPane(tablaReportes), BorderLayout.CENTER);

        JPanel centro = new JPanel(new GridLayout(2, 1, 0, 6));
        centro.add(arriba);
        centro.add(medio);
        add(centro, BorderLayout.CENTER);

        // Abajo: cierre
        JPanel abajo = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 6));
        abajo.setBorder(BorderFactory.createTitledBorder("2. Cerrar el cuadernillo"));
        abajo.add(new JLabel("\u00bfHubo gastos por fiestas o reuniones?"));
        abajo.add(cmbFiesta);
        abajo.add(new JLabel("Principal forma de pago:"));
        abajo.add(cmbPago);
        abajo.add(btnCerrar);
        abajo.add(lblCierre);
        add(abajo, BorderLayout.SOUTH);

        boolean esEncuestador = "Encuestador".equals(rol);
        btnCerrar.setEnabled(esEncuestador);
        if (!esEncuestador) {
            lblCierre.setText("Solo el Encuestador puede cerrar el cuadernillo.");
        }

        btnReportar.setEnabled(false);   // se activa al validar si hay inconsistencias y el usuario es Critico-Digitador
        btnReportar.setVisible("Critico-Digitador".equals(rol));
        btnReportar.addActionListener(e -> reportar());
        cargarReportes();

        btnValidar.addActionListener(e -> validar());
        btnCerrar.addActionListener(e -> cerrar());
    }

    private void validar() {
        modelo.setRowCount(0);
        List<String[]> hallazgos = validacion.validar(idHogar);
        if (hallazgos == null) {
            lblValidacion.setText("No se pudo validar. Revise la conexion con Oracle.");
            return;
        }
        for (String[] h : hallazgos) {
            modelo.addRow(new Object[]{h[0], h[1]});
        }
        ultimosHallazgos = hallazgos;
        btnReportar.setEnabled("Critico-Digitador".equals(rol) && !hallazgos.isEmpty());
        lblValidacion.setText(hallazgos.isEmpty()
                ? "El hogar es consistente: no se encontraron inconsistencias."
                : hallazgos.size() + " inconsistencia(s) encontrada(s).");
    }

    private void reportar() {
        String mensaje = reportes.reportar(idHogar, idUsuario, rol, ultimosHallazgos);
        lblValidacion.setText(mensaje);
        cargarReportes();
        JOptionPane.showMessageDialog(this, mensaje, "Reporte del cr\u00edtico-digitador", JOptionPane.INFORMATION_MESSAGE);
    }

    private void cargarReportes() {
        modeloReportes.setRowCount(0);
        for (String[] r : reportes.consultar(idHogar)) {
            modeloReportes.addRow(new Object[]{r[0], r[1], r[2], r[3]});
        }
    }

    private void cerrar() {
        String fiesta = "No".equals(cmbFiesta.getSelectedItem()) ? "N" : "S";
        String pago = ((String) cmbPago.getSelectedItem()).substring(0, 3);
        int opcion = JOptionPane.showConfirmDialog(this,
                "\u00bfCerrar el cuadernillo del hogar " + idHogar + "?", "Cerrar cuadernillo",
                JOptionPane.YES_NO_OPTION);
        if (opcion != JOptionPane.YES_OPTION) {
            return;
        }
        String[] r = cierre.cerrar(idHogar, fiesta, pago, idUsuario);   // {"1" o "0", mensaje}
        lblCierre.setText(r[1]);
        JOptionPane.showMessageDialog(this, r[1], "Cierre del cuadernillo",
                "1".equals(r[0]) ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.WARNING_MESSAGE);
    }
}
