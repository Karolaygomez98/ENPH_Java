package ui;

import servicios.GastoServicio;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

/**
 * Pestana "Reportes" (RF19): total de gastos del hogar agrupado por tipo de gasto, lugar de compra o dia.
 * La agrupacion se hace en Oracle (GROUP BY) a traves de GastoServicio.reporteAgrupado.
 */
public class PanelReportes extends JPanel {

    private final String idHogar;
    private final GastoServicio servicio = new GastoServicio();

    private final JComboBox<String> cmbAgrupar = new JComboBox<>(new String[]{"Tipo de gasto", "Lugar de compra", "D\u00eda"});
    private final JButton btnGenerar = new JButton("Generar reporte");
    private final JLabel lblTotal = new JLabel(" ");
    private final DefaultTableModel modelo = new DefaultTableModel(new String[]{"Grupo", "Cantidad de gastos", "Total pagado"}, 0) {
        @Override
        public boolean isCellEditable(int f, int c) {
            return false;
        }
    };
    private final JTable tabla = new JTable(modelo);

    public PanelReportes(String idHogar) {
        this.idHogar = idHogar;
        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        JPanel barra = new JPanel(new FlowLayout(FlowLayout.LEFT));
        barra.add(new JLabel("Agrupar los gastos por:"));
        barra.add(cmbAgrupar);
        barra.add(btnGenerar);
        add(barra, BorderLayout.NORTH);

        Estilo.estilizarTabla(tabla);
        add(new JScrollPane(tabla), BorderLayout.CENTER);
        add(lblTotal, BorderLayout.SOUTH);

        btnGenerar.addActionListener(e -> generar());
    }

    private void generar() {
        String[] claves = {"tipo", "lugar", "dia"};
        String agrupar = claves[cmbAgrupar.getSelectedIndex()];
        NumberFormat pesos = NumberFormat.getInstance(new Locale("es", "CO"));
        modelo.setRowCount(0);
        List<Object[]> filas = servicio.reporteAgrupado(idHogar, agrupar);
        double total = 0;
        for (Object[] f : filas) {
            total += (Double) f[2];
            modelo.addRow(new Object[]{f[0], f[1], "$" + pesos.format((Double) f[2])});
        }
        lblTotal.setText(filas.isEmpty()
                ? "No hay gastos para reportar (o no hay conexion con Oracle)."
                : "Total del hogar: $" + pesos.format(total));
    }
}
