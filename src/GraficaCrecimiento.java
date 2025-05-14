import javax.swing.*;
import java.awt.*;
import java.util.List;

public class GraficaCrecimiento extends JPanel {
    private List<Integer> ciudades;
    private List<Long> rutas;
    private List<Double> tiempos;

    public GraficaCrecimiento(List<Integer> ciudades, List<Long> rutas, List<Double> tiempos) {
        this.ciudades = ciudades;
        this.rutas = rutas;
        this.tiempos = tiempos;
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
        g2.drawString("Crecimiento Exponencial del TSP (Fuerza Bruta)", margin, margin - 35);

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

        // Escala logarítmica para Y
        long maxRutas = rutas.stream().mapToLong(Long::longValue).max().getAsLong();
        double maxTiempo = tiempos.stream().mapToDouble(Double::doubleValue).max().getAsDouble();
        double minTiempo = tiempos.stream().mapToDouble(Double::doubleValue).min().getAsDouble();
        if (minTiempo <= 0) minTiempo = 0.0001;

        // Dibujar rutas evaluadas (rojo)
        g2.setColor(Color.RED);
        for (int i = 0; i < ciudades.size() - 1; i++) {
            int x1 = margin + i * w / (ciudades.size() - 1);
            int y1 = getHeight() - margin - (int) (Math.log10(rutas.get(i)) / Math.log10(maxRutas) * h);
            int x2 = margin + (i + 1) * w / (ciudades.size() - 1);
            int y2 = getHeight() - margin - (int) (Math.log10(rutas.get(i + 1)) / Math.log10(maxRutas) * h);
            g2.drawLine(x1, y1, x2, y2);
            g2.fillOval(x1 - 4, y1 - 4, 8, 8);
            // Etiqueta de valor
            g2.setFont(new Font("Arial", Font.PLAIN, 13));
            g2.drawString(String.valueOf(rutas.get(i)), x1 - 10, y1 - 10);
        }
        // Última etiqueta
        int xLast = margin + (ciudades.size() - 1) * w / (ciudades.size() - 1);
        int yLast = getHeight() - margin - (int) (Math.log10(rutas.get(ciudades.size() - 1)) / Math.log10(maxRutas) * h);
        g2.fillOval(xLast - 4, yLast - 4, 8, 8);
        g2.drawString(String.valueOf(rutas.get(ciudades.size() - 1)), xLast - 10, yLast - 10);

        // Dibujar tiempo de ejecución (azul)
        g2.setColor(Color.BLUE);
        for (int i = 0; i < ciudades.size() - 1; i++) {
            int x1 = margin + i * w / (ciudades.size() - 1);
            int y1 = getHeight() - margin - (int) (Math.log10(tiempos.get(i) + minTiempo) / Math.log10(maxTiempo + minTiempo) * h);
            int x2 = margin + (i + 1) * w / (ciudades.size() - 1);
            int y2 = getHeight() - margin - (int) (Math.log10(tiempos.get(i + 1) + minTiempo) / Math.log10(maxTiempo + minTiempo) * h);
            g2.drawLine(x1, y1, x2, y2);
            g2.fillOval(x1 - 4, y1 - 4, 8, 8);
            // Etiqueta de valor
            g2.setFont(new Font("Arial", Font.PLAIN, 13));
            g2.drawString(String.format("%.4f", tiempos.get(i)), x1 - 10, y1 + 20);
        }
        // Última etiqueta
        int yLastT = getHeight() - margin - (int) (Math.log10(tiempos.get(ciudades.size() - 1) + minTiempo) / Math.log10(maxTiempo + minTiempo) * h);
        g2.fillOval(xLast - 4, yLastT - 4, 8, 8);
        g2.drawString(String.format("%.4f", tiempos.get(ciudades.size() - 1)), xLast - 10, yLastT + 20);

        // Etiquetas de X
        g2.setColor(Color.BLACK);
        g2.setFont(new Font("Arial", Font.BOLD, 15));
        for (int i = 0; i < ciudades.size(); i++) {
            int x = margin + i * w / (ciudades.size() - 1);
            g2.drawString("" + ciudades.get(i), x - 8, getHeight() - margin + 25);
        }
        g2.drawString("Número de ciudades", getWidth() / 2 - 60, getHeight() - 20);

        // Leyenda
        g2.setFont(new Font("Arial", Font.BOLD, 16));
        g2.setColor(Color.RED);
        g2.drawString("Rutas evaluadas", margin + 10, margin - 10);
        g2.setColor(Color.BLUE);
        g2.drawString("Tiempo (s)", margin + 180, margin - 10);

        // Eje Y (log)
        g2.setColor(Color.BLACK);
        g2.setFont(new Font("Arial", Font.PLAIN, 13));
        g2.drawString("Escala logarítmica", 10, margin - 30);
    }

    public static void mostrarGrafica(List<Integer> ciudades, List<Long> rutas, List<Double> tiempos) {
        JFrame frame = new JFrame("Crecimiento Exponencial TSP");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.add(new GraficaCrecimiento(ciudades, rutas, tiempos));
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
