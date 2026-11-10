package ui;

import modelos.Caracteristica;
import servicios.CaracteristicaServicio;
import servicios.IdentificacionServicio;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Pestana "Caracteristicas" (capitulo C): para cada grupo de alimentos, con que frecuencia compra el hogar,
 * el valor de la ultima compra y si volvera a comprar en los proximos 14 dias.
 * Guardar crea la respuesta o la actualiza si el grupo ya tenia una (lo decide el DAO).
 */
public class PanelCaracteristicas extends JPanel {

    private static final String[] GRUPOS = {
        "01 - Pan, arepas, bollos y almoj\u00e1banas",
        "02 - Galletas de sal y de dulce",
        "03 - Arroz, pastas, avena, ma\u00edz, harinas y otros cereales",
        "04 - Carne de res, cerdo, hueso y v\u00edsceras",
        "05 - Pollo, otras aves y menudencias",
        "06 - Salchichas, jam\u00f3n y otras carnes fr\u00edas",
        "07 - Pescado y otros productos marinos",
        "08 - Leche (animal y vegetal)",
        "09 - Queso (animal y vegetal)",
        "10 - Otros derivados de la leche",
        "11 - Huevos",
        "12 - Aceites y grasas",
        "13 - Frutas",
        "14 - Verduras y hortalizas",
        "15 - Granos secos",
        "16 - Enlatados y encurtidos",
        "17 - Pl\u00e1tano, yuca, papa y otros tub\u00e9rculos",
        "18 - Az\u00facar, panela y otros endulzantes",
        "19 - Mermeladas, dulces, helados y chocolatinas",
        "20 - Salsas, mayonesa, mostaza y vinagre",
        "21 - Sal y condimentos",
        "22 - Caf\u00e9, chocolate, t\u00e9 y hierbas arom\u00e1ticas",
        "23 - Papas fritas, chitos, man\u00ed y similares",
        "24 - Agua, gaseosas, refrescos y jugos",
        "25 - Alimentos y bebidas preparados fuera del hogar"
    };
    private static final String[] FRECUENCIAS = {
        "1 - Nunca", "2 - Diario", "2.1 - Varias veces por semana", "3 - Semanal",
        "4 - Quincenal", "5 - Mensual", "6 - Bimestral", "7 - Trimestral / Espor\u00e1dico"
    };
    private static final String[] COMPRARA = {"Sin respuesta", "S\u00ed", "No"};

    private final String idHogar;
    private final CaracteristicaServicio servicio = new CaracteristicaServicio();

    private final DefaultTableModel modelo = new DefaultTableModel(
            new String[]{"Grupo", "Frecuencia de compra", "Valor ultima compra", "Comprara en 14 dias"}, 0) {
        @Override
        public boolean isCellEditable(int f, int c) {
            return false;
        }
    };
    private final JTable tabla = new JTable(modelo);

    private final JComboBox<String> cmbGrupo = new JComboBox<>(GRUPOS);
    private final JComboBox<String> cmbFrecuencia = new JComboBox<>(FRECUENCIAS);
    private final JTextField txtValor = new JTextField(10);
    private final JComboBox<String> cmbComprara = new JComboBox<>(COMPRARA);
    private final JButton btnGuardar = new JButton("Guardar");
    private final JButton btnEliminar = new JButton("Eliminar");
    private final JLabel lblMensaje = new JLabel(" ");
    private final IdentificacionServicio identificacion = new IdentificacionServicio();
    private final JTextField txtMercado = new JTextField(10);
    private final JButton btnMercado = new JButton("Guardar valor");

    public PanelCaracteristicas(String idHogar) {
        this.idHogar = idHogar;
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
        btnGuardar.addActionListener(e -> guardar());
        btnEliminar.addActionListener(e -> eliminar());
        btnMercado.addActionListener(e -> guardarMercado());
        java.util.Map<String, String> datos = identificacion.consultar(idHogar);
        if (datos != null && datos.get("valorMercado") != null) {
            txtMercado.setText(datos.get("valorMercado"));
        }
        cargar();
    }

    private JPanel construirFormulario() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBorder(BorderFactory.createTitledBorder("Respuesta del grupo de alimentos"));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(4, 6, 4, 6);
        c.anchor = GridBagConstraints.WEST;

        c.gridy = 0;
        c.gridx = 0; p.add(new JLabel("Grupo:"), c);
        c.gridx = 1; c.gridwidth = 3; p.add(cmbGrupo, c);
        c.gridwidth = 1;

        c.gridy = 1;
        c.gridx = 0; p.add(new JLabel("Frecuencia de compra:"), c);
        c.gridx = 1; p.add(cmbFrecuencia, c);
        c.gridx = 2; p.add(new JLabel("Valor ultima compra ($):"), c);
        c.gridx = 3; p.add(txtValor, c);

        c.gridy = 2;
        c.gridx = 0; p.add(new JLabel("Comprara en los proximos 14 dias:"), c);
        c.gridx = 1; p.add(cmbComprara, c);

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        botones.add(btnGuardar);
        botones.add(btnEliminar);
        c.gridy = 3; c.gridx = 0; c.gridwidth = 4;
        p.add(botones, c);
        c.gridy = 4;
        p.add(lblMensaje, c);

        // RF09: valor total del mercado cuando no se puede desagregar por grupo de alimentos
        c.gridy = 5; c.gridwidth = 1;
        c.gridx = 0; p.add(new JLabel("Valor total del mercado ($), si no se puede desagregar:"), c);
        c.gridx = 1; p.add(txtMercado, c);
        c.gridx = 2; p.add(btnMercado, c);
        return p;
    }

    private void guardarMercado() {
        String error = identificacion.guardarValorMercado(idHogar, txtMercado.getText());
        lblMensaje.setText(error == null ? "Valor del mercado guardado." : error);
    }

    /** Codigo de grupo (01 a 25) que esta al inicio del texto del combo. */
    private static String codigo(String texto) {
        return texto.substring(0, texto.indexOf(' '));
    }

    private static String textoGrupo(String idGrupo) {
        for (String g : GRUPOS) {
            if (g.startsWith(idGrupo + " ")) {
                return g;
            }
        }
        return idGrupo;
    }

    private static String textoFrecuencia(String codigo) {
        for (String f : FRECUENCIAS) {
            if (f.startsWith(codigo + " ")) {
                return f;
            }
        }
        return codigo;
    }

    private void cargar() {
        modelo.setRowCount(0);
        List<Caracteristica> lista = servicio.consultar(idHogar);
        for (Caracteristica k : lista) {
            String comprara = "S".equals(k.getComprara()) ? "S\u00ed" : "N".equals(k.getComprara()) ? "No" : "";
            modelo.addRow(new Object[]{textoGrupo(k.getIdGrupo()), textoFrecuencia(k.getFrecuencia()),
                    k.getValorUltimaCompra() == null ? "" : k.getValorUltimaCompra(), comprara});
        }
    }

    private void llenarFormularioDesdeFila() {
        int f = tabla.getSelectedRow();
        if (f < 0) {
            return;
        }
        cmbGrupo.setSelectedItem(String.valueOf(modelo.getValueAt(f, 0)));
        cmbFrecuencia.setSelectedItem(String.valueOf(modelo.getValueAt(f, 1)));
        txtValor.setText(String.valueOf(modelo.getValueAt(f, 2)));
        String comprara = String.valueOf(modelo.getValueAt(f, 3));
        cmbComprara.setSelectedItem(comprara.isEmpty() ? "Sin respuesta" : comprara);
    }

    private void guardar() {
        Double valor = null;
        String texto = txtValor.getText().trim();
        if (!texto.isEmpty()) {
            try {
                valor = Double.parseDouble(texto.replace(',', '.'));
            } catch (NumberFormatException e) {
                lblMensaje.setText("El valor de la ultima compra debe ser un numero.");
                return;
            }
        }
        String sel = (String) cmbComprara.getSelectedItem();
        String comprara = "S\u00ed".equals(sel) ? "S" : "No".equals(sel) ? "N" : "";
        Caracteristica k = new Caracteristica(idHogar, codigo((String) cmbGrupo.getSelectedItem()),
                codigo((String) cmbFrecuencia.getSelectedItem()), valor, comprara);
        String error = servicio.guardar(k);
        if (error != null) {
            lblMensaje.setText(error);
            return;
        }
        lblMensaje.setText("Respuesta guardada.");
        cargar();
    }

    private void eliminar() {
        String grupo = codigo((String) cmbGrupo.getSelectedItem());
        int opcion = JOptionPane.showConfirmDialog(this,
                "\u00bfEliminar la respuesta del grupo " + grupo + "?", "Eliminar",
                JOptionPane.YES_NO_OPTION);
        if (opcion != JOptionPane.YES_OPTION) {
            return;
        }
        if (servicio.eliminar(idHogar, grupo)) {
            lblMensaje.setText("Respuesta eliminada.");
            cargar();
        } else {
            lblMensaje.setText("No se elimino la respuesta (quiza el grupo no tenia una).");
        }
    }
}
