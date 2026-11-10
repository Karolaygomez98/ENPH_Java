package ui;

import modelos.Encuestador;
import servicios.EncuestadorServicio;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Consulta de usuarios (solo lectura) para el Supervisor: muestra los usuarios del sistema y su rol.
 * Los datos salen de EncuestadorServicio (que llama a EncuestadorDAO y este a Oracle); la ventana no escribe SQL.
 */
public class VentanaUsuarios extends JFrame {

    private final EncuestadorServicio servicio = new EncuestadorServicio();

    private final DefaultTableModel modelo = new DefaultTableModel(new String[]{"Usuario", "Nombre", "Rol"}, 0) {
        @Override
        public boolean isCellEditable(int f, int c) {
            return false;
        }
    };
    private final JTable tabla = new JTable(modelo);
    private final JButton btnActualizar = new JButton("Actualizar lista");
    private final JLabel lblEstado = new JLabel(" ");

    public VentanaUsuarios() {
        setTitle("ENPH - Usuarios del sistema");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(650, 400);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(8, 8));

        add(Estilo.encabezado("Encuesta Nacional de Presupuestos de los Hogares", "Usuarios del sistema"), BorderLayout.NORTH);

        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        Estilo.estilizarTabla(tabla);
        add(new JScrollPane(tabla), BorderLayout.CENTER);

        JPanel sur = new JPanel(new FlowLayout(FlowLayout.LEFT));
        sur.add(btnActualizar);
        sur.add(lblEstado);
        add(sur, BorderLayout.SOUTH);

        btnActualizar.addActionListener(e -> cargar());
        cargar();
    }

    /** Pide los usuarios a Oracle en otro hilo para que la ventana no se congele. */
    private void cargar() {
        lblEstado.setText("Consultando usuarios...");
        btnActualizar.setEnabled(false);
        new SwingWorker<List<Encuestador>, Void>() {
            @Override
            protected List<Encuestador> doInBackground() {
                return servicio.listarEncuestadores();
            }

            @Override
            protected void done() {
                try {
                    List<Encuestador> usuarios = get();
                    modelo.setRowCount(0);
                    for (Encuestador u : usuarios) {
                        modelo.addRow(new Object[]{u.getUsuario(), u.getNombreCompleto(), u.getRol()});
                    }
                    lblEstado.setText(usuarios.isEmpty()
                            ? "No hay usuarios o no hay conexion con Oracle."
                            : usuarios.size() + " usuarios.");
                } catch (Exception ex) {
                    lblEstado.setText("No se pudo consultar: " + ex.getMessage());
                } finally {
                    btnActualizar.setEnabled(true);
                }
            }
        }.execute();
    }
}
