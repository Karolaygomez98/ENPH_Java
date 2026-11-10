package ui;

import servicios.RevisionServicio;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.Map;

/**
 * Revision del Supervisor: lista los cuadernillos cerrados, muestra el resumen del seleccionado y permite
 * finalizar la revision o devolverlo al encuestador para correccion (procedimiento sp_revisar_cuadernillo).
 */
public class VentanaRevision extends JFrame {

    private final RevisionServicio servicio = new RevisionServicio();

    private final DefaultTableModel modelo = new DefaultTableModel(new String[]{"Hogar", "Jefe de hogar", "Revisado"}, 0) {
        @Override
        public boolean isCellEditable(int f, int c) {
            return false;
        }
    };
    private final JTable tabla = new JTable(modelo);
    private final JTextArea txtResumen = new JTextArea();
    private final JButton btnFinalizar = new JButton("Finalizar revisi\u00f3n");
    private final JButton btnDevolver = new JButton("Devolver para correcci\u00f3n");
    private final JButton btnActualizar = new JButton("Actualizar lista");
    private final JLabel lblMensaje = new JLabel(" ");

    private final String idUsuario;

    public VentanaRevision(String idUsuario) {
        this.idUsuario = idUsuario;
        setTitle("ENPH - Revisi\u00f3n del supervisor");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(850, 480);
        setLocationRelativeTo(null);

        Estilo.estilizarTabla(tabla);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        txtResumen.setEditable(false);
        txtResumen.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        JScrollPane izquierda = new JScrollPane(tabla);
        JScrollPane derecha = new JScrollPane(txtResumen);
        derecha.setBorder(BorderFactory.createTitledBorder("Resumen del cuadernillo"));
        JSplitPane division = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, izquierda, derecha);
        division.setDividerLocation(330);
        add(division, BorderLayout.CENTER);

        JPanel sur = new JPanel(new FlowLayout(FlowLayout.LEFT));
        sur.add(btnActualizar);
        sur.add(btnFinalizar);
        sur.add(btnDevolver);
        sur.add(lblMensaje);
        add(sur, BorderLayout.SOUTH);

        tabla.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                mostrarResumen();
            }
        });
        btnActualizar.addActionListener(e -> cargar());
        btnFinalizar.addActionListener(e -> revisar("F", "finalizar la revisi\u00f3n"));
        btnDevolver.addActionListener(e -> revisar("D", "devolver para correcci\u00f3n"));
        cargar();
    }

    private void cargar() {
        modelo.setRowCount(0);
        txtResumen.setText("");
        List<String[]> lista = servicio.disponibles();
        if (lista == null) {
            lblMensaje.setText("No se pudo consultar. Revise la conexion con Oracle.");
            return;
        }
        for (String[] f : lista) {
            modelo.addRow(new Object[]{f[0], f[1], "S".equals(f[2]) ? "S\u00ed" : "No"});
        }
        lblMensaje.setText(lista.isEmpty() ? "No hay cuadernillos cerrados para revisar." : lista.size() + " cuadernillo(s).");
    }

    private String hogarSeleccionado() {
        int f = tabla.getSelectedRow();
        return f < 0 ? null : (String) modelo.getValueAt(f, 0);
    }

    private void mostrarResumen() {
        String id = hogarSeleccionado();
        if (id == null) {
            return;
        }
        Map<String, String> r = servicio.resumen(id);
        if (r == null) {
            txtResumen.setText("No se pudo consultar el resumen.");
            return;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("Hogar: ").append(id).append('\n');
        sb.append("Jefe de hogar: ").append(r.get("jefe")).append('\n');
        sb.append("Direcci\u00f3n: ").append(r.get("direccion")).append('\n');
        sb.append("Estado: ").append(r.get("estado")).append('\n');
        sb.append("Revisado: ").append("S".equals(r.get("revisado")) ? "S\u00ed" : "No").append("\n\n");
        sb.append("Gastos por fiestas: ").append("S".equals(r.get("fiesta")) ? "S\u00ed" : "No").append('\n');
        sb.append("Principal forma de pago: ").append(r.get("pago")).append("\n\n");
        sb.append("Visitas registradas: ").append(r.get("visitas")).append('\n');
        sb.append("Respuestas del cap\u00edtulo C: ").append(r.get("caracteristicas")).append('\n');
        sb.append("Gastos diarios: ").append(r.get("gastos")).append("  (total $").append(r.get("totalGastos")).append(")\n");
        sb.append("D\u00edas con gastos: ").append(r.get("dias")).append('\n');
        sb.append("Comidas fuera del hogar: ").append(r.get("comidas")).append("  (total $").append(r.get("totalComidas")).append(")\n");
        txtResumen.setText(sb.toString());
    }

    private void revisar(String accion, String descripcion) {
        String id = hogarSeleccionado();
        if (id == null) {
            lblMensaje.setText("Seleccione un cuadernillo de la lista.");
            return;
        }
        int opcion = JOptionPane.showConfirmDialog(this, "\u00bfDesea " + descripcion + " del hogar " + id + "?",
                "Revisi\u00f3n", JOptionPane.YES_NO_OPTION);
        if (opcion != JOptionPane.YES_OPTION) {
            return;
        }
        String[] r = servicio.revisar(id, accion, idUsuario);   // {"1" o "0", mensaje}
        cargar();
        lblMensaje.setText(r[1]);
        JOptionPane.showMessageDialog(this, r[1], "Revisi\u00f3n del supervisor", JOptionPane.INFORMATION_MESSAGE);
    }
}
