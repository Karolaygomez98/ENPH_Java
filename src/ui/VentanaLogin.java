package ui;

import dao.LoginDAO.Cuenta;
import servicios.LoginServicio;

import javax.swing.*;
import java.awt.*;

/**
 * Ventana de ingreso a la aplicacion ENPH.
 * Usa los mismos servicios de la version anterior: LoginServicio valida el usuario y la clave
 * contra la tabla USUARIO de Oracle. Si el usuario todavia no tiene clave, se le pide crearla.
 */
public class VentanaLogin extends JFrame {

    // Objetos visuales (componentes Swing)
    private final JTextField txtUsuario = new JTextField(16);
    private final JPasswordField txtClave = new JPasswordField(16);
    private final JButton btnEntrar = new JButton("Entrar");
    private final JLabel lblMensaje = new JLabel(" ");

    // Capa de servicios (logica): no se escribe SQL en la ventana
    private final LoginServicio servicio = new LoginServicio();

    public VentanaLogin() {
        setTitle("ENPH - Cuadernillo 2 | Ingreso");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);

        // Panel con rejilla: etiqueta a la izquierda, campo a la derecha
        JPanel formulario = new JPanel(new GridBagLayout());
        formulario.setBorder(BorderFactory.createEmptyBorder(20, 25, 10, 25));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6, 6, 6, 6);
        c.anchor = GridBagConstraints.WEST;

        JLabel titulo = new JLabel("Ingreso a la aplicaci\u00f3n");
        titulo.setFont(titulo.getFont().deriveFont(Font.BOLD, 14f));
        c.gridx = 0; c.gridy = 0; c.gridwidth = 2;
        formulario.add(titulo, c);

        c.gridwidth = 1;
        c.gridx = 0; c.gridy = 1; formulario.add(new JLabel("Usuario:"), c);
        c.gridx = 1;              formulario.add(txtUsuario, c);
        c.gridx = 0; c.gridy = 2; formulario.add(new JLabel("Clave:"), c);
        c.gridx = 1;              formulario.add(txtClave, c);

        c.gridx = 0; c.gridy = 3; c.gridwidth = 2;
        c.anchor = GridBagConstraints.CENTER;
        formulario.add(btnEntrar, c);

        lblMensaje.setForeground(Color.BLACK);
        c.gridy = 4;
        formulario.add(lblMensaje, c);

        add(Estilo.encabezado("Departamento Administrativo Nacional de Estad\u00edstica", "ENPH - Cuadernillo 2"), BorderLayout.NORTH);
        add(formulario, BorderLayout.CENTER);

        // Eventos: al pulsar el boton o Enter en la clave se intenta ingresar
        btnEntrar.addActionListener(e -> ingresar());
        txtClave.addActionListener(e -> ingresar());
        getRootPane().setDefaultButton(btnEntrar);

        pack();
        setLocationRelativeTo(null); // centrada en la pantalla
    }

    private void ingresar() {
        String usuario = txtUsuario.getText().trim();
        String clave = new String(txtClave.getPassword());
        if (usuario.isEmpty() || clave.isEmpty()) {
            lblMensaje.setText("Escriba el usuario y la clave.");
            return;
        }
        LoginServicio.Resultado r = servicio.ingresar(usuario, clave);

        if (LoginServicio.PRIMER_INGRESO.equals(r.error)) {
            crearClavePrimerIngreso(usuario);
            return;
        }
        if (r.error != null) {
            lblMensaje.setText(r.error);
            txtClave.setText("");
            return;
        }
        abrirPrincipal(r.cuenta);
    }

    /** El usuario existe pero aun no tiene clave: se le pide crearla (minimo 6 caracteres). */
    private void crearClavePrimerIngreso(String usuario) {
        JPasswordField nueva = new JPasswordField(14);
        JPasswordField confirmar = new JPasswordField(14);
        JPanel panel = new JPanel(new GridLayout(0, 1, 4, 4));
        panel.add(new JLabel("Es su primer ingreso. Cree su clave (minimo 6 caracteres):"));
        panel.add(nueva);
        panel.add(new JLabel("Confirme la clave:"));
        panel.add(confirmar);

        int opcion = JOptionPane.showConfirmDialog(this, panel, "Crear clave",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (opcion != JOptionPane.OK_OPTION) {
            return;
        }
        String error = servicio.crearClave(usuario, new String(nueva.getPassword()),
                new String(confirmar.getPassword()));
        if (error != null) {
            JOptionPane.showMessageDialog(this, error, "No se pudo crear la clave", JOptionPane.WARNING_MESSAGE);
            return;
        }
        lblMensaje.setText("Clave creada. Ingrese de nuevo con su clave.");
        txtClave.setText("");
    }

    private void abrirPrincipal(Cuenta cuenta) {
        new VentanaPrincipal(cuenta).setVisible(true);
        dispose(); // cierra la ventana de ingreso
    }
}
