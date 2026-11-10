package ui;

import servicios.IdentificacionServicio;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

/**
 * Pestana "Identificacion (cap. A)": datos del hogar (solo lectura), numero de orden del responsable del gasto (RF03)
 * y resultado del cuadernillo (RF02). Solo el Encuestador puede modificarlos.
 */
public class PanelIdentificacion extends JPanel {

    private final String idHogar;
    private final IdentificacionServicio servicio = new IdentificacionServicio();

    private final JTextField txtCodigo = new JTextField(20);
    private final JTextField txtDireccion = new JTextField(34);
    private final JTextField txtResponsable = new JTextField(34);
    private final JSpinner spnOrden = new JSpinner(new SpinnerNumberModel(1, 1, 99, 1));
    private final JComboBox<String> cmbResultado = new JComboBox<>(IdentificacionServicio.RESULTADOS);
    private final JButton btnGuardar = new JButton("Guardar");
    private final JLabel lblMensaje = new JLabel(" ");

    public PanelIdentificacion(String idHogar) {
        this.idHogar = idHogar;
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        for (JTextField t : new JTextField[]{txtCodigo, txtDireccion, txtResponsable}) {
            t.setEditable(false);
            t.setBackground(new Color(0xe9, 0xec, 0xef));
        }

        JPanel p = new JPanel(new GridBagLayout());
        p.setBorder(BorderFactory.createTitledBorder("Identificaci\u00f3n del hogar"));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6, 8, 6, 8);
        c.anchor = GridBagConstraints.WEST;

        c.gridy = 0; c.gridx = 0; p.add(new JLabel("C\u00f3digo del hogar:"), c);
        c.gridx = 1; p.add(txtCodigo, c);
        c.gridy = 1; c.gridx = 0; p.add(new JLabel("Direcci\u00f3n:"), c);
        c.gridx = 1; p.add(txtDireccion, c);
        c.gridy = 2; c.gridx = 0; p.add(new JLabel("Responsable del gasto:"), c);
        c.gridx = 1; p.add(txtResponsable, c);
        c.gridy = 3; c.gridx = 0; p.add(new JLabel("N\u00famero de orden:"), c);
        c.gridx = 1; p.add(spnOrden, c);
        c.gridy = 4; c.gridx = 0; p.add(new JLabel("Resultado del cuadernillo:"), c);
        c.gridx = 1; p.add(cmbResultado, c);
        c.gridy = 5; c.gridx = 0; p.add(btnGuardar, c);
        c.gridx = 1; p.add(lblMensaje, c);

        JPanel arriba = new JPanel(new FlowLayout(FlowLayout.LEFT));
        arriba.add(p);
        add(arriba, BorderLayout.NORTH);

        btnGuardar.addActionListener(e -> guardar());
        cargar();
    }

    private void cargar() {
        Map<String, String> m = servicio.consultar(idHogar);
        if (m == null) {
            lblMensaje.setText("No se pudo consultar. Revise la conexion y que haya ejecutado actualizacion_bloque1.sql.");
            return;
        }
        txtCodigo.setText(m.get("id"));
        txtDireccion.setText(m.get("direccion"));
        txtResponsable.setText(m.get("responsable"));
        if (m.get("orden") != null) {
            spnOrden.setValue(Integer.parseInt(m.get("orden")));
        }
        String codigo = m.get("resultado");
        if (codigo != null) {
            for (String r : IdentificacionServicio.RESULTADOS) {
                if (r.startsWith(codigo + " ")) {
                    cmbResultado.setSelectedItem(r);
                }
            }
        }
    }

    private void guardar() {
        String error = servicio.guardarIdentificacion(idHogar, (Integer) spnOrden.getValue(),
                (String) cmbResultado.getSelectedItem());
        lblMensaje.setText(error == null ? "Datos guardados." : error);
    }
}
