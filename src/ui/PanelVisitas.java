package ui;

import modelos.Visita;
import servicios.VisitaServicio;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Pestana "Visitas" (capitulo B): lista las visitas de seguimiento del hogar y permite registrar una nueva.
 * Toda la validacion (formatos de fecha y hora, hora fin posterior a hora inicio) la hace VisitaServicio.
 */
public class PanelVisitas extends JPanel {

    private static final String[] COLUMNAS =
            {"N.", "Fecha", "Hora inicio", "Hora fin", "Resultado", "Partes pendientes", "Proxima visita"};
    private static final String[] RESULTADOS =
            {"Completa", "Incompleta", "Cita concertada", "Ausente", "Rechazo"};

    private final String idHogar;
    private final VisitaServicio servicio = new VisitaServicio();

    // Grilla
    private final DefaultTableModel modelo = new DefaultTableModel(COLUMNAS, 0) {
        @Override
        public boolean isCellEditable(int f, int c) {
            return false;
        }
    };
    private final JTable tabla = new JTable(modelo);

    // Formulario
    private final CampoFecha txtFecha = new CampoFecha();
    private final CampoHora txtHoraInicio = new CampoHora();
    private final CampoHora txtHoraFin = new CampoHora();
    private final JComboBox<String> cmbResultado = new JComboBox<>(RESULTADOS);
    private final JTextField txtPendientes = new JTextField(25);
    private final JCheckBox chkProxima = new JCheckBox("Programar proxima visita");
    private final CampoFecha txtFechaProxima = new CampoFecha();
    private final CampoHora txtHoraProxima = new CampoHora();
    private final JButton btnGuardar = new JButton("Guardar visita");
    private final JLabel lblMensaje = new JLabel(" ");

    public PanelVisitas(String idHogar) {
        this.idHogar = idHogar;
        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        Estilo.estilizarTabla(tabla);
        add(new JScrollPane(tabla), BorderLayout.CENTER);
        add(construirFormulario(), BorderLayout.SOUTH);

        btnGuardar.addActionListener(e -> guardar());
        chkProxima.addActionListener(e -> activarProxima());
        activarProxima();
        cargar();
    }

    private JPanel construirFormulario() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBorder(BorderFactory.createTitledBorder("Registrar nueva visita"));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(4, 6, 4, 6);
        c.anchor = GridBagConstraints.WEST;

        c.gridy = 0;
        c.gridx = 0; p.add(new JLabel("Fecha:"), c);
        c.gridx = 1; p.add(txtFecha, c);
        c.gridx = 2; p.add(new JLabel("Hora inicio:"), c);
        c.gridx = 3; p.add(txtHoraInicio, c);
        c.gridx = 4; p.add(new JLabel("Hora fin:"), c);
        c.gridx = 5; p.add(txtHoraFin, c);

        c.gridy = 1;
        c.gridx = 0; p.add(new JLabel("Resultado:"), c);
        c.gridx = 1; p.add(cmbResultado, c);
        c.gridx = 2; p.add(new JLabel("Partes pendientes:"), c);
        c.gridx = 3; c.gridwidth = 3; p.add(txtPendientes, c);
        c.gridwidth = 1;

        c.gridy = 2;
        c.gridx = 0; p.add(chkProxima, c);
        c.gridx = 1; p.add(txtFechaProxima, c);
        c.gridx = 2; p.add(new JLabel("Hora proxima visita:"), c);
        c.gridx = 3; p.add(txtHoraProxima, c);

        c.gridy = 3;
        c.gridx = 0; p.add(btnGuardar, c);
        c.gridx = 1; c.gridwidth = 5; p.add(lblMensaje, c);
        return p;
    }

    /** Consulta las visitas del hogar a traves del servicio y llena la grilla. */
    private void cargar() {
        modelo.setRowCount(0);
        List<Visita> visitas = servicio.consultarVisitas(idHogar);
        int n = 1;
        for (Visita v : visitas) {
            String proxima = (v.getFechaProxima() == null || v.getFechaProxima().isEmpty())
                    ? "" : v.getFechaProxima() + " " + (v.getHoraProxima() == null ? "" : v.getHoraProxima());
            modelo.addRow(new Object[]{n++, v.getFecha(), v.getHoraInicio(), v.getHoraFin(),
                    v.getResultado(), v.getPartesPendientes(), proxima});
        }
    }

    private void guardar() {
        Visita v = new Visita(null, idHogar,
                txtFecha.texto(), txtHoraInicio.texto(), txtHoraFin.texto(),
                (String) cmbResultado.getSelectedItem(), txtPendientes.getText().trim(),
                chkProxima.isSelected() ? txtFechaProxima.texto() : "",
                chkProxima.isSelected() ? txtHoraProxima.texto() : "");
        String error = servicio.guardarNuevaVisita(v);
        if (error != null) {
            lblMensaje.setText(error);
            return;
        }
        lblMensaje.setText("Visita guardada.");
        txtPendientes.setText("");
        chkProxima.setSelected(false);
        activarProxima();
        cargar();
    }

    private void activarProxima() {
        txtFechaProxima.setEnabled(chkProxima.isSelected());
        txtHoraProxima.setEnabled(chkProxima.isSelected());
    }

    /** Campo de fecha con flechas (dd/mm/aaaa). Empieza con la fecha de hoy. */
    static class CampoFecha extends JSpinner {
        CampoFecha() {
            super(new SpinnerDateModel());
            setEditor(new JSpinner.DateEditor(this, "dd/MM/yyyy"));
        }

        String texto() {
            return new SimpleDateFormat("dd/MM/yyyy").format((Date) getValue());
        }
    }

    /** Campo de hora con flechas (hh:mm, 24 horas). Empieza con la hora actual. */
    static class CampoHora extends JSpinner {
        CampoHora() {
            super(new SpinnerDateModel());
            setEditor(new JSpinner.DateEditor(this, "HH:mm"));
        }

        String texto() {
            return new SimpleDateFormat("HH:mm").format((Date) getValue());
        }
    }
}
