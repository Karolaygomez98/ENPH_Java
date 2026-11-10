package ui;

import dao.LoginDAO.Cuenta;
import modelos.Hogar;
import servicios.HogarServicio;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Ventana principal: muestra la lista de hogares con su estado.
 * Los datos salen de HogarServicio (que llama al DAO y este a Oracle); la ventana no escribe SQL.
 */
public class VentanaPrincipal extends JFrame {

    private static final String[] COLUMNAS = {"Codigo del hogar", "Jefe de hogar", "Direccion", "Estado"};

    private final Cuenta cuenta;
    private final HogarServicio hogarServicio = new HogarServicio();

    // Objetos visuales
    private final DefaultTableModel modeloTabla = new DefaultTableModel(COLUMNAS, 0) {
        @Override
        public boolean isCellEditable(int fila, int columna) {
            return false; // la grilla es solo de consulta
        }
    };
    private final JTable tabla = new JTable(modeloTabla);
    private final JLabel lblEstado = new JLabel(" ");
    private final JButton btnActualizar = new JButton("Actualizar lista");
    private final JButton btnAbrir = new JButton("Abrir cuadernillo");
    private final JButton btnRevision = new JButton("Revision del supervisor");
    private final JButton btnInforme = new JButton("Informe gerencial");
    private final JButton btnUsuarios = new JButton("Usuarios");
    private final JButton btnSalir = new JButton("Cerrar sesion");

    public VentanaPrincipal(Cuenta cuenta) {
        this.cuenta = cuenta;
        setTitle("ENPH - Cuadernillo 2");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(850, 500);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(8, 8));

        // Parte superior: quien ingreso
        JLabel bienvenida = new JLabel("Usuario: " + cuenta.nombre + "   |   Rol: " + cuenta.rol);
        bienvenida.setFont(bienvenida.getFont().deriveFont(Font.BOLD, 13f));
        bienvenida.setBorder(BorderFactory.createEmptyBorder(10, 12, 0, 12));
        JPanel norte = new JPanel(new BorderLayout());
        norte.add(Estilo.encabezado("Encuesta Nacional de Presupuestos de los Hogares", "Hogares asignados"), BorderLayout.NORTH);
        norte.add(bienvenida, BorderLayout.SOUTH);
        add(norte, BorderLayout.NORTH);

        // Centro: la grilla con los hogares
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        Estilo.estilizarTabla(tabla);
        add(new JScrollPane(tabla), BorderLayout.CENTER);

        // Parte inferior: botones y mensaje
        JPanel botones = new JPanel(new FlowLayout(FlowLayout.LEFT));
        botones.add(btnActualizar);
        botones.add(btnAbrir);
        botones.add(btnRevision);
        botones.add(btnInforme);
        botones.add(btnUsuarios);
        botones.add(btnSalir);
        JPanel sur = new JPanel(new BorderLayout());
        sur.add(botones, BorderLayout.WEST);
        sur.add(lblEstado, BorderLayout.CENTER);
        sur.setBorder(BorderFactory.createEmptyBorder(0, 8, 8, 8));
        add(sur, BorderLayout.SOUTH);

        // Eventos
        btnActualizar.addActionListener(e -> cargarHogares());
        btnAbrir.addActionListener(e -> abrirCuadernillo());
        btnRevision.addActionListener(e -> new VentanaRevision(cuenta.idUsuario).setVisible(true));
        btnRevision.setVisible("Supervisor".equals(cuenta.rol)); // solo el Supervisor ve este boton
        btnInforme.addActionListener(e -> new VentanaInformeGerencial().setVisible(true));
        btnInforme.setVisible("Supervisor".equals(cuenta.rol)); // solo el Supervisor ve el informe
        btnUsuarios.addActionListener(e -> new VentanaUsuarios().setVisible(true));
        btnUsuarios.setVisible("Supervisor".equals(cuenta.rol)); // solo el Supervisor ve la lista de usuarios
        btnSalir.addActionListener(e -> cerrarSesion());

        cargarHogares();
    }

    /** Pide los hogares a Oracle en otro hilo para que la ventana no se congele. */
    private void cargarHogares() {
        lblEstado.setText("Consultando hogares...");
        btnActualizar.setEnabled(false);
        new SwingWorker<List<Hogar>, Void>() {
            @Override
            protected List<Hogar> doInBackground() {
                return hogarServicio.listarHogares();
            }

            @Override
            protected void done() {
                try {
                    List<Hogar> hogares = get();
                    modeloTabla.setRowCount(0);
                    for (Hogar h : hogares) {
                        modeloTabla.addRow(new Object[]{
                                h.getIdHogar(), h.getJefeHogar(), h.getDireccion(), h.getEstado()});
                    }
                    lblEstado.setText(hogares.isEmpty()
                            ? "No hay hogares o no hay conexion con Oracle."
                            : hogares.size() + " hogares.");
                } catch (Exception ex) {
                    lblEstado.setText("No se pudo consultar: " + ex.getMessage());
                } finally {
                    btnActualizar.setEnabled(true);
                }
            }
        }.execute();
    }

    private void abrirCuadernillo() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) {
            JOptionPane.showMessageDialog(this, "Seleccione un hogar de la lista.",
                    "Abrir cuadernillo", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        String idHogar = (String) modeloTabla.getValueAt(fila, 0);
        String jefe = (String) modeloTabla.getValueAt(fila, 1);
        new VentanaCuadernillo(idHogar, jefe, cuenta.rol, cuenta.idUsuario).setVisible(true);
    }

    private void cerrarSesion() {
        new VentanaLogin().setVisible(true);
        dispose();
    }
}
