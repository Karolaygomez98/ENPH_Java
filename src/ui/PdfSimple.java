package ui;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import javax.imageio.ImageIO;

/** Escribe un PDF de una pagina A4 con una imagen (sin librerias externas). */
public final class PdfSimple {

    private PdfSimple() { }

    public static void escribir(BufferedImage img, File destino) throws IOException {
        ByteArrayOutputStream jpg = new ByteArrayOutputStream();
        BufferedImage rgb = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_RGB);
        rgb.getGraphics().drawImage(img, 0, 0, null);
        ImageIO.write(rgb, "jpg", jpg);
        byte[] datos = jpg.toByteArray();

        double paginaAncho = 595, paginaAlto = 842, margen = 30;
        double escala = Math.min((paginaAncho - 2 * margen) / img.getWidth(), (paginaAlto - 2 * margen) / img.getHeight());
        double w = img.getWidth() * escala, h = img.getHeight() * escala;
        double x = (paginaAncho - w) / 2, y = paginaAlto - margen - h;
        String contenido = String.format(java.util.Locale.US, "q %.2f 0 0 %.2f %.2f %.2f cm /Im0 Do Q", w, h, x, y);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int[] pos = new int[6];
        escribir(out, "%PDF-1.4\n");
        pos[1] = out.size();
        escribir(out, "1 0 obj\n<< /Type /Catalog /Pages 2 0 R >>\nendobj\n");
        pos[2] = out.size();
        escribir(out, "2 0 obj\n<< /Type /Pages /Kids [3 0 R] /Count 1 >>\nendobj\n");
        pos[3] = out.size();
        escribir(out, "3 0 obj\n<< /Type /Page /Parent 2 0 R /MediaBox [0 0 595 842] /Resources << /XObject << /Im0 4 0 R >> >> /Contents 5 0 R >>\nendobj\n");
        pos[4] = out.size();
        escribir(out, "4 0 obj\n<< /Type /XObject /Subtype /Image /Width " + img.getWidth() + " /Height " + img.getHeight()
                + " /ColorSpace /DeviceRGB /BitsPerComponent 8 /Filter /DCTDecode /Length " + datos.length + " >>\nstream\n");
        out.write(datos);
        escribir(out, "\nendstream\nendobj\n");
        pos[5] = out.size();
        escribir(out, "5 0 obj\n<< /Length " + contenido.length() + " >>\nstream\n" + contenido + "\nendstream\nendobj\n");
        int xref = out.size();
        StringBuilder sb = new StringBuilder("xref\n0 6\n0000000000 65535 f \n");
        for (int i = 1; i <= 5; i++) {
            sb.append(String.format("%010d 00000 n \n", pos[i]));
        }
        sb.append("trailer\n<< /Size 6 /Root 1 0 R >>\nstartxref\n").append(xref).append("\n%%EOF\n");
        escribir(out, sb.toString());
        try (FileOutputStream f = new FileOutputStream(destino)) {
            out.writeTo(f);
        }
    }

    private static void escribir(ByteArrayOutputStream out, String s) throws IOException {
        out.write(s.getBytes(StandardCharsets.ISO_8859_1));
    }
}
