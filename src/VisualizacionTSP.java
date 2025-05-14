import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;
import javax.imageio.ImageIO;

public class VisualizacionTSP extends JPanel {
    private List<Ciudad> recorrido;
    private double distancia;
    private long rutasEvaluadas;
    private double tiempoSegundos;
    private double memoriaConsumidaMB;

    public VisualizacionTSP(List<Ciudad> recorrido, double distancia, long rutasEvaluadas, double tiempoSegundos, double memoriaConsumidaMB) {
        this.recorrido = recorrido;
        this.distancia = distancia;
        this.rutasEvaluadas = rutasEvaluadas;
        this.tiempoSegundos = tiempoSegundos;
        this.memoriaConsumidaMB = memoriaConsumidaMB;
        setPreferredSize(new Dimension(1400, 900)); // Ventana más grande
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (recorrido == null || recorrido.size() == 0) return;

        Graphics2D g2 = (Graphics2D) g;
        g2.setFont(new Font("Arial", Font.BOLD, 20));

        // Escalar coordenadas para que quepan en la ventana
        double minLat = recorrido.stream().mapToDouble(Ciudad::getLatitud).min().getAsDouble();
        double maxLat = recorrido.stream().mapToDouble(Ciudad::getLatitud).max().getAsDouble();
        double minLon = recorrido.stream().mapToDouble(Ciudad::getLongitud).min().getAsDouble();
        double maxLon = recorrido.stream().mapToDouble(Ciudad::getLongitud).max().getAsDouble();

        int margin = 120;
        int width = getWidth() - 2 * margin;
        int height = getHeight() - 2 * margin - 180;

        // Dibujar las ciudades y el recorrido
        for (int i = 0; i < recorrido.size(); i++) {
            Ciudad c = recorrido.get(i);
            int x = margin + (int) ((c.getLongitud() - minLon) / (maxLon - minLon) * width);
            int y = margin + (int) ((maxLat - c.getLatitud()) / (maxLat - minLat) * height);

            // Ciudad de inicio/fin en verde y más grande
            if (i == 0) {
                g2.setColor(Color.GREEN.darker());
                g2.fillOval(x - 10, y - 10, 20, 20);
            } else {
                g2.setColor(Color.RED);
                g2.fillOval(x - 7, y - 7, 14, 14);
            }
            g2.setColor(Color.BLACK);
            g2.drawString((i + 1) + ". " + c.getNombre(), x + 12, y - 8);

            // Dibujar línea al siguiente
            if (i < recorrido.size() - 1) {
                Ciudad siguiente = recorrido.get(i + 1);
                int x2 = margin + (int) ((siguiente.getLongitud() - minLon) / (maxLon - minLon) * width);
                int y2 = margin + (int) ((maxLat - siguiente.getLatitud()) / (maxLat - minLat) * height);
                g2.setColor(Color.BLUE);
                g2.drawLine(x, y, x2, y2);
            }
        }
        // Línea de regreso al origen
        Ciudad primero = recorrido.get(0);
        Ciudad ultimo = recorrido.get(recorrido.size() - 1);
        int x1 = margin + (int) ((primero.getLongitud() - minLon) / (maxLon - minLon) * width);
        int y1 = margin + (int) ((maxLat - primero.getLatitud()) / (maxLat - minLat) * height);
        int x2 = margin + (int) ((ultimo.getLongitud() - minLon) / (maxLon - minLon) * width);
        int y2 = margin + (int) ((maxLat - ultimo.getLatitud()) / (maxLat - minLat) * height);
        g2.setColor(Color.BLUE);
        g2.drawLine(x1, y1, x2, y2);

        // Mostrar datos en la parte inferior
        g2.setColor(Color.BLACK);
        int yDatos = getHeight() - 160;
        g2.setFont(new Font("Arial", Font.BOLD, 22));
        g2.drawString("Cantidad de ciudades: " + recorrido.size(), 60, yDatos);
        g2.drawString(String.format("Distancia mínima: %.2f km", distancia), 60, yDatos + 30);
        g2.drawString("Rutas evaluadas: " + rutasEvaluadas, 60, yDatos + 60);
        g2.drawString(String.format("Tiempo de ejecución: %.3f segundos", tiempoSegundos), 400, yDatos + 30);
        g2.drawString(String.format("Memoria consumida: %.2f MB", memoriaConsumidaMB), 400, yDatos + 60);

        // Mostrar recorrido óptimo en varias líneas si es necesario
        g2.setFont(new Font("Arial", Font.BOLD, 20));
        g2.drawString("Recorrido óptimo:", 60, yDatos + 100);
        StringBuilder recorridoStr = new StringBuilder();
        for (Ciudad c : recorrido) {
            recorridoStr.append(c.getNombre()).append(" -> ");
        }
        recorridoStr.append(recorrido.get(0).getNombre());

        // Dividir el recorrido en varias líneas si es muy largo y evitar cortar nombres
        String recorridoCompleto = recorridoStr.toString();
        int maxLineLength = 70; // caracteres por línea
        int yLinea = yDatos + 130;
        while (recorridoCompleto.length() > 0) {
            int corte = Math.min(maxLineLength, recorridoCompleto.length());
            if (corte < recorridoCompleto.length()) {
                int ultimoGuion = recorridoCompleto.lastIndexOf("->", corte);
                if (ultimoGuion > 0) corte = ultimoGuion + 2;
            }
            String linea = recorridoCompleto.substring(0, corte).trim();
            g2.drawString(linea, 60, yLinea);
            recorridoCompleto = recorridoCompleto.substring(corte).trim();
            yLinea += 26;
        }
    }

    public static void mostrarRecorrido(List<Ciudad> recorrido, double distancia, long rutasEvaluadas, double tiempoSegundos, double memoriaConsumidaMB) {
        JFrame frame = new JFrame("Visualización del Recorrido TSP");
        VisualizacionTSP panel = new VisualizacionTSP(recorrido, distancia, rutasEvaluadas, tiempoSegundos, memoriaConsumidaMB);

        // Panel de botones
        JPanel botones = new JPanel();
        JButton btnGuardar = new JButton("Guardar imagen");
        JButton btnCerrar = new JButton("Cerrar");

        btnGuardar.addActionListener(e -> {
            BufferedImage image = new BufferedImage(panel.getWidth(), panel.getHeight(), BufferedImage.TYPE_INT_RGB);
            Graphics2D g2 = image.createGraphics();
            panel.paint(g2);
            g2.dispose();
            try {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setDialogTitle("Guardar imagen como");
                if (fileChooser.showSaveDialog(panel) == JFileChooser.APPROVE_OPTION) {
                    File file = fileChooser.getSelectedFile();
                    ImageIO.write(image, "png", file);
                    JOptionPane.showMessageDialog(panel, "Imagen guardada correctamente.");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(panel, "Error al guardar la imagen: " + ex.getMessage());
            }
        });

        btnCerrar.addActionListener(e -> frame.dispose());

        botones.add(btnGuardar);
        botones.add(btnCerrar);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        frame.add(panel, BorderLayout.CENTER);
        frame.add(botones, BorderLayout.SOUTH);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
