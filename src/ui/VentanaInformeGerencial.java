package ui;

import dao.InformeDAO;
import dao.InformeDAO.Dato;
import dao.InformeDAO.Informe;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Arc2D;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

/**
 * Informe gerencial del Supervisor: resume el gasto registrado en los cuadernillos
 * con tarjetas, graficos (Java2D) y conclusiones generadas a partir de los datos.
 */
public class VentanaInformeGerencial extends JFrame {

    private static final Color[] PALETA = {
        Estilo.VINO, Estilo.AMARILLO, Estilo.VERDE, new Color(0x2a, 0x6f, 0x97),
        new Color(0xc0, 0x6c, 0x84), new Color(0x8a, 0x8d, 0x91), new Color(0xe0, 0x7a, 0x1f)
    };
    private static final NumberFormat PESOS = NumberFormat.getIntegerInstance(new Locale("es", "CO"));

    private final InformeDAO dao = new InformeDAO();
    private final JPanel contenido = new JPanel();
    private final JComboBox<String> cmbAlcance = new JComboBox<>(new String[] {
        "Todos los cuadernillos", "Solo cuadernillos cerrados" });
    private final JLabel lblEstado = new JLabel(" ");

    public VentanaInformeGerencial() {
        setTitle("ENPH - Informe gerencial");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(980, 800);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        add(Estilo.encabezado("Encuesta Nacional de Presupuestos de los Hogares",
                "Informe gerencial - Estructura del gasto de los hogares"), BorderLayout.NORTH);

        contenido.setLayout(new BoxLayout(contenido, BoxLayout.Y_AXIS));
        contenido.setBackground(Estilo.FONDO);
        contenido.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));
        JScrollPane scroll = new JScrollPane(contenido);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        add(scroll, BorderLayout.CENTER);

        JButton btnActualizar = new JButton("Actualizar informe");
        JPanel sur = new JPanel(new FlowLayout(FlowLayout.LEFT));
        sur.add(new JLabel("Alcance:"));
        sur.add(cmbAlcance);
        sur.add(btnActualizar);
        JButton btnPdf = new JButton("Generar PDF");
        sur.add(btnPdf);
        sur.add(lblEstado);
        add(sur, BorderLayout.SOUTH);

        btnActualizar.addActionListener(e -> cargar());
        btnPdf.addActionListener(e -> generarPdf());
        cmbAlcance.addActionListener(e -> cargar());
        cargar();
    }

    private void cargar() {
        lblEstado.setText("Consultando Oracle...");
        final boolean soloCerrados = cmbAlcance.getSelectedIndex() == 1;
        new SwingWorker<Informe, Void>() {
            @Override
            protected Informe doInBackground() {
                return dao.generar(soloCerrados);
            }

            @Override
            protected void done() {
                try {
                    Informe inf = get();
                    if (inf == null) {
                        lblEstado.setText("No se pudo generar el informe (revise la conexi\u00f3n).");
                        return;
                    }
                    pintar(inf);
                    lblEstado.setText("Informe generado.");
                } catch (Exception ex) {
                    lblEstado.setText("Error: " + ex.getMessage());
                }
            }
        }.execute();
    }

    private void pintar(Informe inf) {
        contenido.removeAll();
        if (inf.registros == 0) {
            JLabel vacio = new JLabel("A\u00fan no hay gastos registrados para el alcance elegido.");
            vacio.setFont(vacio.getFont().deriveFont(Font.BOLD, 14f));
            contenido.add(vacio);
            contenido.revalidate();
            contenido.repaint();
            return;
        }

        JLabel fecha = new JLabel("Informe generado el "
                + new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm").format(new java.util.Date())
                + "  |  " + cmbAlcance.getSelectedItem());
        fecha.setForeground(Color.GRAY);
        fecha.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 60));
        fecha.setAlignmentX(Component.LEFT_ALIGNMENT);
        contenido.add(fecha);
        contenido.add(Box.createVerticalStrut(8));

        // 1 y 2: distribucion del presupuesto (torta) y prioridades del hogar
        JPanel fila1 = new JPanel(new GridLayout(1, 2, 10, 0));
        fila1.setOpaque(false);
        fila1.add(marco("1", "Distribuci\u00f3n del presupuesto", new Dona(inf.porLugar, "Gasto total registrado", inf.total)));
        fila1.add(marco("2", "Prioridades del hogar", prioridades(inf)));
        fila1.setPreferredSize(new Dimension(900, 310));
        fila1.setMaximumSize(new Dimension(Integer.MAX_VALUE, 310));
        fila1.setAlignmentX(Component.LEFT_ALIGNMENT);
        contenido.add(fila1);
        contenido.add(Box.createVerticalStrut(10));

        // 3: hallazgos clave (tarjetas con cifras grandes)
        JPanel hall = new JPanel(new GridLayout(1, 0, 8, 0));
        hall.setOpaque(false);
        Dato lugar = inf.porLugar.get(0);
        Dato maxDia = inf.porDia.get(0);
        for (Dato d : inf.porDia) {
            if (d.valor > maxDia.valor) {
                maxDia = d;
            }
        }
        hall.add(tarjeta("$ " + PESOS.format(inf.total), "Gasto total registrado en " + inf.registros + " registros"));
        hall.add(tarjeta(lugar.etiqueta, "Lugar donde m\u00e1s se gasta (" + pct(lugar.valor, inf.total) + ")"));
        hall.add(tarjeta(maxDia.etiqueta.matches("[Dd]\\d+") ? "D\u00eda " + maxDia.etiqueta.substring(1) : maxDia.etiqueta, "D\u00eda de mayor gasto ($ " + PESOS.format(maxDia.valor) + ")"));
        if (!inf.porPago.isEmpty()) {
            hall.add(tarjeta(inf.porPago.get(0).etiqueta, "Forma de pago principal m\u00e1s frecuente"));
        }
        hall.add(tarjeta(inf.hogaresCerrados + " de " + inf.hogares,
                "Cuadernillos cerrados (" + inf.hogaresRevisados + " revisados)"));
        JPanel panelHall = marco("3", "Hallazgos clave", hall);
        panelHall.setPreferredSize(new Dimension(900, 130));
        panelHall.setMaximumSize(new Dimension(Integer.MAX_VALUE, 130));
        panelHall.setAlignmentX(Component.LEFT_ALIGNMENT);
        contenido.add(panelHall);
        contenido.add(Box.createVerticalStrut(10));

        // 4: conclusiones (panel oscuro)
        // JLabel con HTML: respeta el color blanco en cualquier tema (Nimbus ignora el color de JTextArea)
        JLabel txt = new JLabel("<html><body>" + conclusiones(inf)
                .replace("&", "&amp;").replace("<", "&lt;").replace("\n", "<br>") + "</body></html>");
        txt.setForeground(Color.WHITE);
        txt.setFont(txt.getFont().deriveFont(Font.PLAIN, 13f));
        txt.setVerticalAlignment(SwingConstants.TOP);
        txt.setBorder(BorderFactory.createEmptyBorder(2, 12, 8, 12));
        JPanel concl = marco("4", "Conclusiones", txt);
        concl.setBackground(Estilo.VINO);
        ((JLabel) ((JPanel) concl.getComponent(0)).getComponent(1)).setForeground(Estilo.AMARILLO);
        concl.setPreferredSize(new Dimension(900, 190));
        concl.setMaximumSize(new Dimension(Integer.MAX_VALUE, 190));
        concl.setAlignmentX(Component.LEFT_ALIGNMENT);
        contenido.add(concl);

        contenido.revalidate();
        contenido.repaint();
    }

    /** Pide donde guardar y escribe el informe que se ve en pantalla como PDF. */
    private void generarPdf() {
        if (contenido.getComponentCount() == 0) {
            lblEstado.setText("No hay informe para exportar.");
            return;
        }
        JFileChooser fc = new JFileChooser();
        fc.setSelectedFile(new java.io.File("Informe_gerencial_ENPH.pdf"));
        if (fc.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }
        java.io.File destino = fc.getSelectedFile();
        if (!destino.getName().toLowerCase().endsWith(".pdf")) {
            destino = new java.io.File(destino.getPath() + ".pdf");
        }
        try {
            guardarPdf(destino);
            lblEstado.setText("PDF guardado: " + destino.getName());
        } catch (java.io.IOException ex) {
            lblEstado.setText("No se pudo guardar el PDF: " + ex.getMessage());
        }
    }

    /** Dibuja el informe a doble resolucion y lo escribe como PDF. */
    public void guardarPdf(java.io.File destino) throws java.io.IOException {
        int banda = 70;
        Dimension tam = new Dimension(900, 700);
        Dimension anterior = contenido.getSize();
        contenido.setSize(tam);
        distribuir(contenido);
        java.awt.image.BufferedImage img = new java.awt.image.BufferedImage(tam.width * 2, (tam.height + banda) * 2,
                java.awt.image.BufferedImage.TYPE_INT_RGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.scale(2, 2);
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, tam.width, tam.height + banda);
        g.setColor(Estilo.VINO);
        g.fillRect(0, 0, tam.width, banda);
        g.setColor(Estilo.AMARILLO);
        g.setFont(g.getFont().deriveFont(Font.BOLD, 12f));
        g.drawString("ENCUESTA NACIONAL DE PRESUPUESTOS DE LOS HOGARES - CUADERNILLO 2", 20, 26);
        g.setColor(Color.WHITE);
        g.setFont(g.getFont().deriveFont(Font.BOLD, 22f));
        g.drawString("Informe gerencial: estructura del gasto de los hogares", 20, 54);
        g.translate(0, banda);
        contenido.printAll(g);
        g.dispose();
        contenido.setSize(anterior);
        contenido.revalidate();
        PdfSimple.escribir(img, destino);
    }

    private void distribuir(Container c) {
        c.doLayout();
        for (Component k : c.getComponents()) {
            if (k instanceof Container) {
                distribuir((Container) k);
            }
        }
    }

    /** En que prioriza el hogar su gasto: dos rankings, cada uno suma el 100%. */
    private JComponent prioridades(Informe inf) {
        JPanel p = new JPanel(new GridLayout(0, 1, 0, 5));
        p.setOpaque(false);
        p.setBorder(BorderFactory.createEmptyBorder(2, 12, 10, 12));
        agregarRanking(p, "SEG\u00daN EL ORIGEN DEL GASTO", inf.porOrigen, inf.total);
        agregarRanking(p, "SEG\u00daN EL TIPO DE GASTO", inf.porTipo, inf.total);
        return p;
    }

    private void agregarRanking(JPanel p, String titulo, List<Dato> datos, double total) {
        JLabel t = new JLabel(titulo);
        t.setFont(t.getFont().deriveFont(Font.BOLD, 11f));
        t.setForeground(Color.GRAY);
        p.add(t);
        int n = 1;
        for (Dato d : datos) {
            final boolean primero = n == 1;
            JPanel fila = new JPanel(new BorderLayout(10, 0)) {
                @Override
                protected void paintComponent(Graphics g0) {
                    Graphics2D g = (Graphics2D) g0.create();
                    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g.setColor(primero ? new Color(0xfb, 0xee, 0xd0) : new Color(0xf1, 0xf3, 0xf5));
                    g.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
                    g.dispose();
                    super.paintComponent(g0);
                }
            };
            fila.setOpaque(false);
            fila.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 12));
            JLabel num = new JLabel(String.valueOf(n), SwingConstants.CENTER) {
                @Override
                protected void paintComponent(Graphics g0) {
                    Graphics2D g = (Graphics2D) g0.create();
                    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g.setColor(getBackground());
                    g.fillOval(0, 0, getWidth() - 1, getHeight() - 1);
                    g.dispose();
                    super.paintComponent(g0);
                }
            };
            num.setOpaque(false);
            num.setBackground(primero ? Estilo.VINO : Color.GRAY);
            num.setForeground(Color.WHITE);
            num.setFont(num.getFont().deriveFont(Font.BOLD, 13f));
            num.setPreferredSize(new Dimension(24, 24));
            JLabel nombre = new JLabel(d.etiqueta + "  ($ " + PESOS.format(d.valor) + ")");
            nombre.setFont(nombre.getFont().deriveFont(primero ? Font.BOLD : Font.PLAIN, 13f));
            JLabel porc = new JLabel(pct(d.valor, total));
            porc.setFont(porc.getFont().deriveFont(Font.BOLD, 18f));
            porc.setForeground(primero ? Estilo.VINO : Color.DARK_GRAY);
            fila.add(num, BorderLayout.WEST);
            fila.add(nombre, BorderLayout.CENTER);
            fila.add(porc, BorderLayout.EAST);
            p.add(fila);
            n++;
        }
    }

    private JTextArea texto(String contenidoTxt) {
        JTextArea txt = new JTextArea(contenidoTxt);
        txt.setEditable(false);
        txt.setLineWrap(true);
        txt.setWrapStyleWord(true);
        txt.setFont(txt.getFont().deriveFont(13f));
        txt.setBorder(BorderFactory.createEmptyBorder(2, 12, 8, 12));
        return txt;
    }

    /** Tarjeta de un hallazgo: cifra grande y una linea de explicacion. */
    private JPanel tarjeta(String valor, String detalle) {
        JPanel p = new JPanel(new BorderLayout(0, 4));
        p.setBackground(new Color(0xf7, 0xf8, 0xfa));
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(4, 0, 0, 0, Estilo.AMARILLO),
                BorderFactory.createEmptyBorder(8, 10, 8, 8)));
        JLabel v = new JLabel(valor);
        v.setFont(v.getFont().deriveFont(Font.BOLD, 16f));
        v.setForeground(Estilo.VINO);
        JLabel d = new JLabel("<html><body style='width:120px'>" + detalle.replace("&", "&amp;").replace("<", "&lt;") + "</body></html>");
        d.setVerticalAlignment(SwingConstants.TOP);
        d.setForeground(Color.DARK_GRAY);
        d.setFont(d.getFont().deriveFont(Font.PLAIN, 11f));
        p.add(v, BorderLayout.NORTH);
        p.add(d, BorderLayout.CENTER);
        return p;
    }

    /** Conclusiones redactadas a partir de los datos (no es texto fijo). */
    private String conclusiones(Informe inf) {
        StringBuilder sb = new StringBuilder();
        Dato lugar = inf.porLugar.get(0);
        Dato origen = inf.porOrigen.get(0);
        Dato tipo = inf.porTipo.get(0);
        sb.append("- El gasto de los hogares se concentra en ").append(lugar.etiqueta).append(", que re\u00fane el ")
          .append(pct(lugar.valor, inf.total)).append(" del presupuesto registrado.\n");
        sb.append("- La mayor parte del dinero se destina a '").append(origen.etiqueta).append("' (")
          .append(pct(origen.valor, inf.total)).append(") y el gasto de tipo '").append(tipo.etiqueta)
          .append("' predomina con el ").append(pct(tipo.valor, inf.total)).append(".\n");
        sb.append("- El informe incluye ").append(inf.registros).append(" registros de gasto; ")
          .append(inf.hogaresCerrados).append(" de ").append(inf.hogares).append(" cuadernillos est\u00e1n cerrados.\n");
        sb.append("- A medida que se cierren y revisen m\u00e1s hogares, los porcentajes se acercan al comportamiento real.");
        return sb.toString();
    }

    private String pct(double parte, double total) {
        return total <= 0 ? "0%" : String.format(Locale.US, "%.1f%%", parte * 100.0 / total);
    }

    /** Seccion numerada: insignia amarilla y titulo. */
    private JPanel marco(String numero, String titulo, JComponent hijo) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(Color.WHITE);
        JPanel cab = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        cab.setOpaque(false);
        JLabel badge = new JLabel(numero, SwingConstants.CENTER) {
            @Override
            protected void paintComponent(Graphics g0) {
                Graphics2D g = (Graphics2D) g0.create();
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g.setColor(Estilo.AMARILLO);
                g.fillOval(0, 0, getWidth() - 1, getHeight() - 1);
                g.dispose();
                super.paintComponent(g0);
            }
        };
        badge.setOpaque(false);
        badge.setForeground(Estilo.VINO);
        badge.setFont(badge.getFont().deriveFont(Font.BOLD, 13f));
        badge.setPreferredSize(new Dimension(24, 24));
        JLabel t = new JLabel(titulo);
        t.setFont(t.getFont().deriveFont(Font.BOLD, 15f));
        t.setForeground(Estilo.VERDE);
        t.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 40));
        cab.add(badge);
        cab.add(t);
        p.add(cab, BorderLayout.NORTH);
        p.add(hijo, BorderLayout.CENTER);
        p.setBorder(BorderFactory.createLineBorder(new Color(0xdd, 0xdd, 0xdd)));
        return p;
    }

    /** Grafico de dona con porcentajes sobre cada porcion y leyenda. */
    private static class Dona extends JComponent {
        /** Porcentajes redondeados que siempre suman 100 (metodo del mayor residuo). */
        private static double[] repartir(List<Dato> datos, double suma, int dec) {
            double esc = Math.pow(10, dec);
            int n = datos.size();
            long[] base = new long[n];
            double[] res = new double[n];
            long usado = 0;
            for (int i = 0; i < n; i++) {
                double v = datos.get(i).valor * 100.0 * esc / suma;
                base[i] = (long) Math.floor(v);
                res[i] = v - base[i];
                usado += base[i];
            }
            long falta = Math.round(100 * esc) - usado;
            while (falta > 0) {
                int mejor = 0;
                for (int i = 1; i < n; i++) {
                    if (res[i] > res[mejor]) mejor = i;
                }
                base[mejor]++;
                res[mejor] = -1;
                falta--;
            }
            double[] out = new double[n];
            for (int i = 0; i < n; i++) out[i] = base[i] / esc;
            return out;
        }

        private final List<Dato> datos;
        private final String titulo;
        private final double total;

        Dona(List<Dato> datos, String titulo, double total) {
            this.datos = datos;
            this.titulo = titulo;
            this.total = total;
        }

        @Override
        protected void paintComponent(Graphics g0) {
            Graphics2D g = (Graphics2D) g0.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            double suma = 0;
            for (Dato d : datos) {
                suma += d.valor;
            }
            if (suma <= 0) {
                g.dispose();
                return;
            }
            int lado = Math.min(getHeight() - 14, getWidth() / 2 + 10);
            int x = 12, y = (getHeight() - lado) / 2;
            double ang = 90;
            double centro = lado / 2.0;
            for (int i = 0; i < datos.size(); i++) {
                double barrido = -360.0 * datos.get(i).valor / suma;
                g.setColor(PALETA[i % PALETA.length]);
                g.fill(new Arc2D.Double(x, y, lado, lado, ang, barrido, Arc2D.PIE));
                ang += barrido;
            }
            // separadores blancos y hueco
            g.setColor(Color.WHITE);
            g.setStroke(new BasicStroke(2f));
            double a2 = 90;
            for (Dato d : datos) {
                double rad = Math.toRadians(a2);
                g.drawLine((int) (x + centro), (int) (y + centro),
                        (int) (x + centro + centro * Math.cos(rad)), (int) (y + centro - centro * Math.sin(rad)));
                a2 -= 360.0 * d.valor / suma;
            }
            int hueco = (int) (lado * 0.52);
            g.fillOval(x + (lado - hueco) / 2, y + (lado - hueco) / 2, hueco, hueco);
            double[] entero = repartir(datos, suma, 0);
            double[] decimal = repartir(datos, suma, 1);
            // porcentajes sobre las porciones
            g.setFont(g.getFont().deriveFont(Font.BOLD, 12f));
            double a3 = 90;
            for (int i = 0; i < datos.size(); i++) {
                double barrido = 360.0 * datos.get(i).valor / suma;
                double medio = Math.toRadians(a3 - barrido / 2);
                double r = centro * 0.76;
                if (barrido >= 9) {
                    String t = String.format(Locale.US, "%.0f%%", entero[i]);
                    g.setFont(g.getFont().deriveFont(Font.BOLD, barrido >= 22 ? 12f : 10f));
                    FontMetrics fm = g.getFontMetrics();
                    g.setColor(i % PALETA.length == 1 ? Estilo.VINO : Color.WHITE);
                    g.drawString(t, (int) (x + centro + r * Math.cos(medio)) - fm.stringWidth(t) / 2,
                            (int) (y + centro - r * Math.sin(medio)) + fm.getAscent() / 2 - 2);
                }
                a3 -= barrido;
            }
            // centro
            g.setColor(Color.GRAY);
            g.setFont(g.getFont().deriveFont(Font.PLAIN, 10f));
            FontMetrics f1 = g.getFontMetrics();
            int cx = x + lado / 2, cy = y + lado / 2;
            g.drawString(titulo, cx - f1.stringWidth(titulo) / 2, cy - 4);
            g.setColor(Estilo.VINO);
            g.setFont(g.getFont().deriveFont(Font.BOLD, 15f));
            String tt = "$ " + PESOS.format(total);
            FontMetrics f2 = g.getFontMetrics();
            g.drawString(tt, cx - f2.stringWidth(tt) / 2, cy + 14);
            // leyenda
            g.setFont(g.getFont().deriveFont(Font.PLAIN, 12f));
            int ly = Math.max(14, (getHeight() - datos.size() * 24) / 2);
            int lx = x + lado + 18;
            for (int i = 0; i < datos.size(); i++) {
                g.setColor(PALETA[i % PALETA.length]);
                g.fillRoundRect(lx, ly + i * 24, 13, 13, 4, 4);
                g.setColor(Color.DARK_GRAY);
                g.drawString(datos.get(i).etiqueta + "  (" + String.format(Locale.US, "%.1f%%", decimal[i]) + ")", lx + 20, ly + i * 24 + 11);
            }
            g.dispose();
        }
    }
}
