package ui;

import modelos.GastoDiario;
import servicios.GastoServicio;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

/**
 * Pestana "Gastos diarios" (capitulo D): CRUD de los gastos de un hogar.
 * Crear = Guardar nuevo | Leer = la grilla | Actualizar = Guardar cambios | Borrar = Eliminar.
 * Las reglas de negocio las aplica GastoServicio; el acceso a Oracle lo hace GastoDiarioDAO.
 */
public class PanelGastos extends JPanel {

    private static final String[] COLUMNAS =
            {"Id", "Dia", "Categoria", "Articulo", "Cantidad", "Unidad", "Valor pagado", "Lugar de compra", "Forma de adquisicion", "Frecuencia", "Equivalencia"};
    private static final String[] CATEGORIAS = {"Hogar", "Personal"};
    private static final String[] UNIDADES = servicios.CatalogoServicio.unidades();
    private static final String[] LUGARES = servicios.CatalogoServicio.lugares();

    private static final String[] FORMAS = servicios.CatalogoServicio.formasAdquisicion();
    private static final String[] FRECUENCIAS = servicios.CatalogoServicio.frecuencias();

    private final String idHogar;
    private final GastoServicio servicio = new GastoServicio();

    // Grilla
    private final DefaultTableModel modelo = new DefaultTableModel(COLUMNAS, 0) {
        @Override
        public boolean isCellEditable(int f, int c) {
            return false;
        }
    };
    private final JTable tabla = new JTable(modelo);

    // Formulario
    private final JSpinner spnDia = new JSpinner(new SpinnerNumberModel(1, 1, 14, 1));
    private final JComboBox<String> cmbCategoria = new JComboBox<>(CATEGORIAS);
    private final JTextField txtArticulo = new JTextField(22);
    private final JTextField txtCantidad = new JTextField(7);
    private final JComboBox<String> cmbUnidad = new JComboBox<>(UNIDADES);
    private final JTextField txtValor = new JTextField(9);
    private final JComboBox<String> cmbLugar = new JComboBox<>(LUGARES);
    private final JComboBox<String> cmbForma = new JComboBox<>(FORMAS);
    private final JComboBox<String> cmbFrecuencia = new JComboBox<>(FRECUENCIAS);
    private final JTextField txtEquivalencia = new JTextField(7);

    // Filtros (RF18)
    private final JComboBox<String> fltDia = new JComboBox<>(new String[]{"Todos", "1", "2", "3", "4", "5", "6", "7",
            "8", "9", "10", "11", "12", "13", "14"});
    private final JComboBox<String> fltCategoria = new JComboBox<>(new String[]{"Todas", "Hogar", "Personal"});
    private final JTextField fltArticulo = new JTextField(14);
    private final JButton btnFiltrar = new JButton("Filtrar");
    private final JButton btnQuitarFiltro = new JButton("Quitar filtro");
    private final JButton btnNuevo = new JButton("Agregar");
    private final JButton btnCambios = new JButton("Modificar");
    private final JButton btnEliminar = new JButton("Eliminar");
    private final JButton btnLimpiar = new JButton("Limpiar");
    private final JLabel lblTotal = new JLabel("Total: $0");
    private final JLabel lblMensaje = new JLabel(" ");

    public PanelGastos(String idHogar) {
        this.idHogar = idHogar;
        // RF04: solo se pueden diligenciar los dias que ya llegaron (del 1 hasta hoy)
        ((SpinnerNumberModel) spnDia.getModel()).setMaximum(new servicios.IdentificacionServicio().diasHabilitados(idHogar));
        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        Estilo.estilizarTabla(tabla);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JPanel centro = new JPanel(new BorderLayout(0, 6));
        JPanel barra = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        barra.add(new JLabel("Filtrar - Dia:"));
        barra.add(fltDia);
        barra.add(new JLabel("Categoria:"));
        barra.add(fltCategoria);
        barra.add(new JLabel("Articulo:"));
        barra.add(fltArticulo);
        barra.add(btnFiltrar);
        barra.add(btnQuitarFiltro);
        centro.add(barra, BorderLayout.NORTH);
        centro.add(new JScrollPane(tabla), BorderLayout.CENTER);
        add(centro, BorderLayout.CENTER);
        btnFiltrar.addActionListener(e -> cargar());
        fltArticulo.addActionListener(e -> cargar());
        btnQuitarFiltro.addActionListener(e -> {
            fltDia.setSelectedIndex(0);
            fltCategoria.setSelectedIndex(0);
            fltArticulo.setText("");
            cargar();
        });
        add(construirFormulario(), BorderLayout.SOUTH);

        // Eventos
        tabla.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                llenarFormularioDesdeFila();
            }
        });
        btnNuevo.addActionListener(e -> guardarNuevo());
        btnCambios.addActionListener(e -> guardarCambios());
        btnEliminar.addActionListener(e -> eliminar());
        btnLimpiar.addActionListener(e -> limpiar());

        limpiar();
        cargar();
    }

    private JPanel construirFormulario() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBorder(BorderFactory.createTitledBorder("Gasto"));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(4, 6, 4, 6);
        c.anchor = GridBagConstraints.WEST;

        c.gridy = 0;
        c.gridx = 0; p.add(new JLabel("Dia habilitado:"), c);
        c.gridx = 1; p.add(spnDia, c);
        c.gridx = 2; p.add(new JLabel("Categoria:"), c);
        c.gridx = 3; p.add(cmbCategoria, c);
        c.gridx = 4; p.add(new JLabel("Articulo:"), c);
        c.gridx = 5; p.add(txtArticulo, c);

        c.gridy = 1;
        c.gridx = 0; p.add(new JLabel("Cantidad:"), c);
        c.gridx = 1; p.add(txtCantidad, c);
        c.gridx = 2; p.add(new JLabel("Unidad:"), c);
        c.gridx = 3; p.add(cmbUnidad, c);
        c.gridx = 4; p.add(new JLabel("Valor pagado ($):"), c);
        c.gridx = 5; p.add(txtValor, c);

        c.gridy = 2;
        c.gridx = 0; p.add(new JLabel("Lugar de compra:"), c);
        c.gridx = 1; c.gridwidth = 3; p.add(cmbLugar, c);
        c.gridwidth = 1;
        c.gridx = 4; p.add(new JLabel("Forma de adquisicion:"), c);
        c.gridx = 5; p.add(cmbForma, c);

        c.gridy = 3;
        c.gridx = 0; p.add(new JLabel("Frecuencia de compra:"), c);
        c.gridx = 1; c.gridwidth = 3; p.add(cmbFrecuencia, c);
        c.gridwidth = 1;
        c.gridx = 4; p.add(new JLabel("Equivalencia (g o cm3):"), c);
        c.gridx = 5; p.add(txtEquivalencia, c);

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        botones.add(btnNuevo);
        botones.add(btnCambios);
        botones.add(btnEliminar);
        botones.add(btnLimpiar);
        c.gridy = 4; c.gridx = 0; c.gridwidth = 6;
        p.add(botones, c);

        c.gridy = 5;
        JPanel pie = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        pie.add(lblTotal);
        pie.add(lblMensaje);
        p.add(pie, c);
        return p;
    }

    /** Lee los gastos del hogar en Oracle y llena la grilla y el total. */
    private void cargar() {
        modelo.setRowCount(0);
        Integer dia = fltDia.getSelectedIndex() == 0 ? null : Integer.valueOf((String) fltDia.getSelectedItem());
        String categoria = fltCategoria.getSelectedIndex() == 0 ? null : (String) fltCategoria.getSelectedItem();
        List<GastoDiario> gastos = servicio.buscarGastos(idHogar, dia, categoria, fltArticulo.getText().trim());
        for (GastoDiario g : gastos) {
            modelo.addRow(new Object[]{g.getIdGasto(), g.getDia(), g.getCategoria(),
                    g.getDescripcionProducto(), g.getCantidad(), g.getUnidadMedida(),
                    g.getValorPagado(), g.getLugarCompra(), g.getFormaAdquisicion(),
                    g.getFrecuenciaCompra() == null ? "No aplica" : g.getFrecuenciaCompra(),
                    g.getEquivalencia() == null ? "" : g.getEquivalencia()});
        }
        NumberFormat pesos = NumberFormat.getInstance(new Locale("es", "CO"));
        lblTotal.setText("Total del hogar: $" + pesos.format(servicio.calcularTotalGastoHogar(idHogar)));
    }

    private void llenarFormularioDesdeFila() {
        int f = tabla.getSelectedRow();
        if (f < 0) {
            return;
        }
        spnDia.setValue(((Number) modelo.getValueAt(f, 1)).intValue());
        cmbCategoria.setSelectedItem(String.valueOf(modelo.getValueAt(f, 2)));
        txtArticulo.setText(String.valueOf(modelo.getValueAt(f, 3)));
        txtCantidad.setText(String.valueOf(modelo.getValueAt(f, 4)));
        cmbUnidad.setSelectedItem(String.valueOf(modelo.getValueAt(f, 5)));
        txtValor.setText(String.valueOf(modelo.getValueAt(f, 6)));
        cmbLugar.setSelectedItem(String.valueOf(modelo.getValueAt(f, 7)));
        cmbForma.setSelectedItem(String.valueOf(modelo.getValueAt(f, 8)));
        cmbFrecuencia.setSelectedItem(String.valueOf(modelo.getValueAt(f, 9)));
        txtEquivalencia.setText(String.valueOf(modelo.getValueAt(f, 10)));
    }

    /** Arma el objeto GastoDiario con lo escrito en el formulario; devuelve null si hay un error. */
    private GastoDiario leerFormulario(String idGasto) {
        double cantidad;
        double valor;
        try {
            cantidad = Double.parseDouble(txtCantidad.getText().trim().replace(',', '.'));
            valor = Double.parseDouble(txtValor.getText().trim().replace(',', '.'));
        } catch (NumberFormatException e) {
            lblMensaje.setText("La cantidad y el valor deben ser numeros.");
            return null;
        }
        if (txtArticulo.getText().trim().isEmpty()) {
            lblMensaje.setText("Escriba el articulo.");
            return null;
        }
        if (cantidad <= 0 || valor <= 0) {
            lblMensaje.setText("La cantidad y el valor deben ser mayores a 0.");
            return null;
        }
        GastoDiario g = new GastoDiario(idGasto, idHogar, (Integer) spnDia.getValue(),
                (String) cmbCategoria.getSelectedItem(), txtArticulo.getText().trim(), cantidad,
                (String) cmbUnidad.getSelectedItem(), valor, (String) cmbLugar.getSelectedItem());
        String eq = txtEquivalencia.getText().trim().replace(',', '.');
        if (!eq.isEmpty()) {
            try {
                double e = Double.parseDouble(eq);
                if (e <= 0) {
                    lblMensaje.setText("La equivalencia debe ser mayor a 0 (o dejela en blanco).");
                    return null;
                }
                g.setEquivalencia(e);
            } catch (NumberFormatException ex) {
                lblMensaje.setText("La equivalencia debe ser un numero (o dejela en blanco).");
                return null;
            }
        }
        g.setFormaAdquisicion((String) cmbForma.getSelectedItem());
        String fr = (String) cmbFrecuencia.getSelectedItem();
        g.setFrecuenciaCompra("No aplica".equals(fr) ? null : fr);
        String regla = servicio.reglasNegocio(g);   // RF12 y RF13
        if (regla != null) {
            lblMensaje.setText(regla);
            return null;
        }
        return g;
    }

    private void guardarNuevo() {
        GastoDiario g = leerFormulario(null);
        if (g == null) {
            return;
        }
        if (servicio.guardarNuevoGasto(g)) {
            lblMensaje.setText("Gasto guardado.");
            limpiar();
            cargar();
        } else {
            lblMensaje.setText("No se guardo el gasto. Revise los datos o la conexion con Oracle.");
        }
    }

    private void guardarCambios() {
        int f = tabla.getSelectedRow();
        if (f < 0) {
            lblMensaje.setText("Seleccione un gasto de la lista para modificarlo.");
            return;
        }
        GastoDiario g = leerFormulario(String.valueOf(modelo.getValueAt(f, 0)));
        if (g == null) {
            return;
        }
        if (servicio.actualizarGasto(g)) {
            lblMensaje.setText("Gasto actualizado.");
            cargar();
        } else {
            lblMensaje.setText("No se actualizo el gasto.");
        }
    }

    private void eliminar() {
        int f = tabla.getSelectedRow();
        if (f < 0) {
            lblMensaje.setText("Seleccione un gasto de la lista para eliminarlo.");
            return;
        }
        int opcion = JOptionPane.showConfirmDialog(this, "\u00bfEliminar el gasto seleccionado?",
                "Eliminar gasto", JOptionPane.YES_NO_OPTION);
        if (opcion != JOptionPane.YES_OPTION) {
            return;
        }
        if (servicio.eliminarGasto(String.valueOf(modelo.getValueAt(f, 0)))) {
            lblMensaje.setText("Gasto eliminado.");
            limpiar();
            cargar();
        } else {
            lblMensaje.setText("No se elimino el gasto.");
        }
    }

    private void limpiar() {
        tabla.clearSelection();
        spnDia.setValue(1);
        txtArticulo.setText("");
        txtCantidad.setText("");
        txtValor.setText("");
        cmbCategoria.setSelectedIndex(0);
        Estilo.seleccionar(cmbUnidad, "Unidad");
        Estilo.seleccionar(cmbLugar, "Tienda de barrio");
        Estilo.seleccionar(cmbForma, "Comprado");
        cmbFrecuencia.setSelectedIndex(0);
        txtEquivalencia.setText("");
    }
}
