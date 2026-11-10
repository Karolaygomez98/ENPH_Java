package ui;

import modelos.ComidaPreparada;
import servicios.ComidaServicio;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Pestana "Comidas fuera del hogar" (capitulo D1): CRUD de las comidas preparadas fuera del hogar.
 * ComidaServicio valida y devuelve el mensaje de error (o null si todo salio bien).
 */
public class PanelComidas extends JPanel {

    private static final String[] COLUMNAS = {"Id", "Dia", "Tipo de comida", "Descripcion", "Cantidad", "Valor pagado",
            "Categoria", "Domicilio", "Lugar de compra", "Forma de adquisicion", "Frecuencia"};
    private static final String[] TIPOS = servicios.CatalogoServicio.tiposComida();

    private static final String[] CATEGORIAS = {"Personal", "Hogar"};
    private static final String[] LUGARES = servicios.CatalogoServicio.lugares();
    private static final String[] FORMAS = servicios.CatalogoServicio.formasAdquisicion();
    private static final String[] FRECUENCIAS = servicios.CatalogoServicio.frecuencias();

    private final String idHogar;
    private final ComidaServicio servicio = new ComidaServicio();

    private final DefaultTableModel modelo = new DefaultTableModel(COLUMNAS, 0) {
        @Override
        public boolean isCellEditable(int f, int c) {
            return false;
        }
    };
    private final JTable tabla = new JTable(modelo);

    private final JSpinner spnDia = new JSpinner(new SpinnerNumberModel(1, 1, 14, 1));
    private final JComboBox<String> cmbTipo = new JComboBox<>(TIPOS);
    private final JTextField txtEstablecimiento = new JTextField(24);
    private final JTextField txtValor = new JTextField(9);
    private final JSpinner spnCantidad = new JSpinner(new SpinnerNumberModel(1, 1, 99, 1));
    private final JComboBox<String> cmbCategoria = new JComboBox<>(CATEGORIAS);
    private final JCheckBox chkDomicilio = new JCheckBox("Pedido a domicilio");
    private final JComboBox<String> cmbLugar = new JComboBox<>(LUGARES);
    private final JComboBox<String> cmbForma = new JComboBox<>(FORMAS);
    private final JComboBox<String> cmbFrecuencia = new JComboBox<>(FRECUENCIAS);
    private final JButton btnNuevo = new JButton("Agregar");
    private final JButton btnCambios = new JButton("Modificar");
    private final JButton btnEliminar = new JButton("Eliminar");
    private final JButton btnLimpiar = new JButton("Limpiar");
    private final JLabel lblMensaje = new JLabel(" ");

    public PanelComidas(String idHogar) {
        this.idHogar = idHogar;
        // RF04: solo se pueden diligenciar los dias que ya llegaron (del 1 hasta hoy)
        ((SpinnerNumberModel) spnDia.getModel()).setMaximum(new servicios.IdentificacionServicio().diasHabilitados(idHogar));
        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        Estilo.estilizarTabla(tabla);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        add(new JScrollPane(tabla), BorderLayout.CENTER);
        add(construirFormulario(), BorderLayout.SOUTH);

        tabla.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                llenarFormularioDesdeFila();
            }
        });
        btnNuevo.addActionListener(e -> guardarNueva());
        btnCambios.addActionListener(e -> guardarCambios());
        btnEliminar.addActionListener(e -> eliminar());
        btnLimpiar.addActionListener(e -> limpiar());

        limpiar();
        cargar();
    }

    private JPanel construirFormulario() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBorder(BorderFactory.createTitledBorder("Comida fuera del hogar"));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(4, 6, 4, 6);
        c.anchor = GridBagConstraints.WEST;

        c.gridy = 0;
        c.gridx = 0; p.add(new JLabel("Dia habilitado:"), c);
        c.gridx = 1; p.add(spnDia, c);
        c.gridx = 2; p.add(new JLabel("Tipo de comida:"), c);
        c.gridx = 3; p.add(cmbTipo, c);

        c.gridy = 1;
        c.gridx = 0; p.add(new JLabel("Descripcion:"), c);
        c.gridx = 1; c.gridwidth = 3; p.add(txtEstablecimiento, c);
        c.gridwidth = 1;
        c.gridx = 4; p.add(new JLabel("Valor pagado ($):"), c);
        c.gridx = 5; p.add(txtValor, c);

        c.gridy = 2;
        c.gridx = 0; p.add(new JLabel("Cantidad:"), c);
        c.gridx = 1; p.add(spnCantidad, c);
        c.gridx = 2; p.add(new JLabel("Categoria:"), c);
        c.gridx = 3; p.add(cmbCategoria, c);
        c.gridx = 4; c.gridwidth = 2; p.add(chkDomicilio, c);
        c.gridwidth = 1;

        c.gridy = 3;
        c.gridx = 0; p.add(new JLabel("Lugar de compra:"), c);
        c.gridx = 1; c.gridwidth = 3; p.add(cmbLugar, c);
        c.gridwidth = 1;
        c.gridx = 4; p.add(new JLabel("Forma de adquisicion:"), c);
        c.gridx = 5; p.add(cmbForma, c);

        c.gridy = 4;
        c.gridx = 0; p.add(new JLabel("Frecuencia de compra:"), c);
        c.gridx = 1; c.gridwidth = 3; p.add(cmbFrecuencia, c);
        c.gridwidth = 1;

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        botones.add(btnNuevo);
        botones.add(btnCambios);
        botones.add(btnEliminar);
        botones.add(btnLimpiar);
        c.gridy = 5; c.gridx = 0; c.gridwidth = 6;
        p.add(botones, c);
        c.gridy = 6;
        p.add(lblMensaje, c);
        return p;
    }

    private void cargar() {
        modelo.setRowCount(0);
        List<ComidaPreparada> comidas = servicio.consultarComidas(idHogar);
        for (ComidaPreparada m : comidas) {
            modelo.addRow(new Object[]{m.getIdComida(), m.getDia(), m.getTipo(),
                    m.getEstablecimiento(), m.getCantidad(), m.getValor(),
                    m.getCategoria() == null ? "Personal" : m.getCategoria(),
                    m.isDomicilio() ? "S\u00ed" : "No",
                    m.getLugarCompra() == null ? "No especificado" : m.getLugarCompra(),
                    m.getFormaAdquisicion() == null ? "Comprado" : m.getFormaAdquisicion(),
                    m.getFrecuenciaCompra() == null ? "No aplica" : m.getFrecuenciaCompra()});
        }
    }

    private void llenarFormularioDesdeFila() {
        int f = tabla.getSelectedRow();
        if (f < 0) {
            return;
        }
        spnDia.setValue(((Number) modelo.getValueAt(f, 1)).intValue());
        cmbTipo.setSelectedItem(String.valueOf(modelo.getValueAt(f, 2)));
        txtEstablecimiento.setText(String.valueOf(modelo.getValueAt(f, 3)));
        spnCantidad.setValue(((Number) modelo.getValueAt(f, 4)).intValue());
        txtValor.setText(String.valueOf(modelo.getValueAt(f, 5)));
        cmbCategoria.setSelectedItem(String.valueOf(modelo.getValueAt(f, 6)));
        chkDomicilio.setSelected(!"No".equals(String.valueOf(modelo.getValueAt(f, 7))));
        cmbLugar.setSelectedItem(String.valueOf(modelo.getValueAt(f, 8)));
        cmbForma.setSelectedItem(String.valueOf(modelo.getValueAt(f, 9)));
        cmbFrecuencia.setSelectedItem(String.valueOf(modelo.getValueAt(f, 10)));
    }

    /** Arma la comida con lo escrito; devuelve null si el valor no es un numero. */
    private ComidaPreparada leerFormulario(String idComida) {
        double valor;
        try {
            valor = Double.parseDouble(txtValor.getText().trim().replace(',', '.'));
        } catch (NumberFormatException e) {
            lblMensaje.setText("El valor debe ser un numero.");
            return null;
        }
        ComidaPreparada m = new ComidaPreparada(idComida, idHogar, (Integer) spnDia.getValue(),
                (String) cmbTipo.getSelectedItem(), txtEstablecimiento.getText().trim(), valor);
        m.setCantidad((Integer) spnCantidad.getValue());
        m.setCategoria((String) cmbCategoria.getSelectedItem());
        m.setDomicilio(chkDomicilio.isSelected());
        m.setLugarCompra((String) cmbLugar.getSelectedItem());
        m.setFormaAdquisicion((String) cmbForma.getSelectedItem());
        String fr = (String) cmbFrecuencia.getSelectedItem();
        m.setFrecuenciaCompra("No aplica".equals(fr) ? null : fr);
        return m;
    }

    private void guardarNueva() {
        ComidaPreparada m = leerFormulario(null);
        if (m == null) {
            return;
        }
        String error = servicio.guardarNuevaComida(m);
        if (error != null) {
            lblMensaje.setText(error);
            return;
        }
        lblMensaje.setText("Comida guardada.");
        limpiar();
        cargar();
    }

    private void guardarCambios() {
        int f = tabla.getSelectedRow();
        if (f < 0) {
            lblMensaje.setText("Seleccione una comida de la lista para modificarla.");
            return;
        }
        ComidaPreparada m = leerFormulario(String.valueOf(modelo.getValueAt(f, 0)));
        if (m == null) {
            return;
        }
        String error = servicio.actualizar(m);
        if (error != null) {
            lblMensaje.setText(error);
            return;
        }
        lblMensaje.setText("Comida actualizada.");
        cargar();
    }

    private void eliminar() {
        int f = tabla.getSelectedRow();
        if (f < 0) {
            lblMensaje.setText("Seleccione una comida de la lista para eliminarla.");
            return;
        }
        int opcion = JOptionPane.showConfirmDialog(this, "\u00bfEliminar la comida seleccionada?",
                "Eliminar comida", JOptionPane.YES_NO_OPTION);
        if (opcion != JOptionPane.YES_OPTION) {
            return;
        }
        if (servicio.eliminar(String.valueOf(modelo.getValueAt(f, 0)))) {
            lblMensaje.setText("Comida eliminada.");
            limpiar();
            cargar();
        } else {
            lblMensaje.setText("No se elimino la comida.");
        }
    }

    private void limpiar() {
        tabla.clearSelection();
        spnDia.setValue(1);
        cmbTipo.setSelectedIndex(0);
        txtEstablecimiento.setText("");
        txtValor.setText("");
        spnCantidad.setValue(1);
        cmbCategoria.setSelectedIndex(0);
        chkDomicilio.setSelected(false);
        Estilo.seleccionar(cmbLugar, "No especificado");
        Estilo.seleccionar(cmbForma, "Comprado");
        cmbFrecuencia.setSelectedIndex(0);
    }
}
