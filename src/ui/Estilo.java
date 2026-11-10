package ui;

import javax.swing.*;
import javax.swing.table.JTableHeader;
import java.awt.*;

/**
 * Colores y piezas visuales comunes (tomados de la pagina web del proyecto):
 * franja vino arriba, etiqueta amarilla, encabezados de tabla en verde y fondo gris claro.
 */
public final class Estilo {

    public static final Color VINO = new Color(0x4a, 0x0e, 0x17);
    public static final Color AMARILLO = new Color(0xff, 0xb7, 0x03);
    public static final Color VERDE = new Color(0x1e, 0x52, 0x35);
    public static final Color FONDO = new Color(0xf4, 0xf6, 0xf8);

    private Estilo() { }

    /** Franja superior: emblema (si existe img/emblema.png), etiqueta y titulo. */
    public static JPanel encabezado(String etiqueta, String titulo) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 8));
        p.setBackground(VINO);

        ImageIcon icono = new ImageIcon("img/emblema.png");
        if (icono.getIconWidth() > 0) {
            Image esc = icono.getImage().getScaledInstance(56, 56, Image.SCALE_SMOOTH);
            p.add(new JLabel(new ImageIcon(esc)));
        }

        JPanel textos = new JPanel(new GridLayout(0, 1));
        textos.setOpaque(false);
        JLabel lblEtiqueta = new JLabel(etiqueta.toUpperCase());
        lblEtiqueta.setForeground(AMARILLO);
        lblEtiqueta.setFont(lblEtiqueta.getFont().deriveFont(Font.BOLD, 11f));
        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setFont(lblTitulo.getFont().deriveFont(Font.BOLD, 16f));
        textos.add(lblEtiqueta);
        textos.add(lblTitulo);
        p.add(textos);
        return p;
    }

    /** Pinta el encabezado de una tabla en verde con letra blanca (Nimbus lo ignora, por eso se usa un renderer propio). */
    public static void estilizarTabla(JTable tabla) {
        JTableHeader h = tabla.getTableHeader();
        h.setDefaultRenderer(new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object valor, boolean sel,
                                                           boolean foco, int fila, int col) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(t, valor, false, false, fila, col);
                l.setOpaque(true);
                l.setBackground(VERDE);
                l.setForeground(Color.WHITE);
                l.setFont(l.getFont().deriveFont(Font.BOLD));
                l.setHorizontalAlignment(SwingConstants.CENTER);
                l.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(0x2d, 0x6a, 0x4f)));
                return l;
            }
        });
        tabla.setGridColor(new Color(0xdd, 0xdd, 0xdd));
        tabla.setRowHeight(24);
    }

    /** Deja un panel en modo consulta: desactiva botones, campos y listas de captura (las tablas siguen visibles). */
    public static void soloLectura(java.awt.Container c) {
        for (java.awt.Component k : c.getComponents()) {
            if (k instanceof javax.swing.JButton || k instanceof javax.swing.JTextField
                    || k instanceof javax.swing.JComboBox || k instanceof javax.swing.JSpinner
                    || k instanceof javax.swing.JCheckBox) {
                k.setEnabled(false);
            }
            if (k instanceof java.awt.Container) {
                soloLectura((java.awt.Container) k);
            }
        }
    }

    /** Deja seleccionado el elemento con ese nombre (si existe); si no, el primero. */
    public static void seleccionar(javax.swing.JComboBox<String> combo, String nombre) {
        for (int i = 0; i < combo.getItemCount(); i++) {
            if (nombre.equalsIgnoreCase(combo.getItemAt(i))) {
                combo.setSelectedIndex(i);
                return;
            }
        }
        if (combo.getItemCount() > 0) {
            combo.setSelectedIndex(0);
        }
    }
}
