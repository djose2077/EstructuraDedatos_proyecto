import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.awt.Color;

public class GraficaSimple extends JPanel {
    private List<Integer> ciudades;
    private List<? extends Number> valores;
    private String titulo, etiquetaX, etiquetaY;
    private Color colorLinea;

    public GraficaSimple(List<Integer> ciudades, List<? extends Number> valores, String titulo, String etiquetaX, String etiquetaY, Color colorLinea) {
        this.ciudades = ciudades;
        this.valores = valores;
        this.titulo = titulo;
        this.etiquetaX = etiquetaX;
        this.etiquetaY = etiquetaY;
        this.colorLinea = colorLinea;
        setPreferredSize(new Dimension(900, 500));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (ciudades == null || ciudades.size() < 2) return;

        Graphics2D g2 = (Graphics2D) g;
        int margin = 80;
        int w = getWidth() - 2 * margin;
        int h = getHeight() - 2 * margin;

        // Título
        g2.setFont(new Font("Arial", Font.BOLD, 22));
        g2.drawString(titulo, margin, margin - 35);

        // Ejes
        g2.setColor(Color.BLACK);
        g2.drawLine(margin, getHeight() - margin, getWidth() - margin, getHeight() - margin); // X
        g2.drawLine(margin, margin, margin, getHeight() - margin); // Y

        // Líneas de cuadrícula
        g2.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{2}, 0));
        g2.setColor(new Color(200, 200, 200));
        int gridLines = 8;
        for (int i = 1; i < gridLines; i++) {
            int y = margin + i * h / gridLines;
            g2.drawLine(margin, y, getWidth() - margin, y);
        }
        for (int i = 1; i < ciudades.size(); i++) {
            int x = margin + i * w / (ciudades.size() - 1);
            g2.drawLine(x, margin, x, getHeight() - margin);
        }
        g2.setStroke(new BasicStroke(2));

        // Escalado lineal
        double maxValor = valores.stream().mapToDouble(Number::doubleValue).max().getAsDouble();

        // Dibujar la curva
        g2.setColor(colorLinea);
        for (int i = 0; i < ciudades.size() - 1; i++) {
            int x1 = margin + i * w / (ciudades.size() - 1);
            int y1 = getHeight() - margin - (int) (valores.get(i).doubleValue() * h / maxValor);
            int x2 = margin + (i + 1) * w / (ciudades.size() - 1);
            int y2 = getHeight() - margin - (int) (valores.get(i + 1).doubleValue() * h / maxValor);
            g2.drawLine(x1, y1, x2, y2);
            g2.fillOval(x1 - 4, y1 - 4, 8, 8);
            // Etiqueta de valor
            g2.setFont(new Font("Arial", Font.PLAIN, 13));
            g2.drawString(String.valueOf(valores.get(i)), x1 - 10, y1 - 10);
        }
        // Última etiqueta
        int xLast = margin + (ciudades.size() - 1) * w / (ciudades.size() - 1);
        int yLast = getHeight() - margin - (int) (valores.get(ciudades.size() - 1).doubleValue() * h / maxValor);
        g2.fillOval(xLast - 4, yLast - 4, 8, 8);
        g2.drawString(String.valueOf(valores.get(ciudades.size() - 1)), xLast - 10, yLast - 10);

        // Etiquetas de X
        g2.setColor(Color.BLACK);
        g2.setFont(new Font("Arial", Font.BOLD, 15));
        for (int i = 0; i < ciudades.size(); i++) {
            int x = margin + i * w / (ciudades.size() - 1);
            g2.drawString("" + ciudades.get(i), x - 8, getHeight() - margin + 25);
        }
        g2.drawString(etiquetaX, getWidth() / 2 - 60, getHeight() - 20);

        // Etiqueta Y
        g2.setFont(new Font("Arial", Font.BOLD, 15));
        g2.drawString(etiquetaY, margin - 60, margin - 10);
    }

    public static void mostrarGrafica(List<Integer> ciudades, List<? extends Number> valores, String titulo, String etiquetaX, String etiquetaY, Color colorLinea) {
        JFrame frame = new JFrame(titulo);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.add(new GraficaSimple(ciudades, valores, titulo, etiquetaX, etiquetaY, colorLinea));
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}