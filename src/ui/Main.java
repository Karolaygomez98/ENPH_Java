package ui;

import javax.swing.*;

/** Punto de entrada de la aplicacion de escritorio. */
public class Main {
    public static void main(String[] args) {
        // Tema visual Nimbus (viene incluido en Java): botones, tablas y listas con aspecto moderno
        try {
            // Fuente y colores: deben ponerse ANTES de activar Nimbus para que los botones midan bien el texto
            UIManager.put("defaultFont", new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 14));
            UIManager.put("nimbusBase", new java.awt.Color(0x4a, 0x0e, 0x17));
            UIManager.put("control", new java.awt.Color(0xf4, 0xf6, 0xf8));
            UIManager.put("Table.alternateRowColor", new java.awt.Color(0xf1, 0xf3, 0xf5));
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            // si no esta disponible, se usa el tema por defecto
        }
        // Swing debe iniciarse en su propio hilo (hilo de eventos)
        SwingUtilities.invokeLater(() -> new VentanaLogin().setVisible(true));
    }
}
