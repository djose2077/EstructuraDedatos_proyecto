import java.util.*;
import java.io.*;
import javax.swing.*;
import java.awt.Color;

public class App {
    /**
     * Carga una lista de ciudades desde un archivo CSV/TXT.
     * El formato esperado por línea es: id,nombre,latitud,longitud
     * Muestra mensajes de error si hay problemas de formato o lectura.
     */
    public static List<Ciudad> cargarCiudadesDesdeArchivo(File archivo) {
        List<Ciudad> ciudades = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            String linea; // Línea leída del archivo
            int lineaNum = 1;
            while ((linea = br.readLine()) != null) {
                String[] partes = linea.split(",");
                if (partes.length == 4) {
                    try {
                        int id = Integer.parseInt(partes[0].trim());
                        String nombre = partes[1].trim();
                        double lat = Double.parseDouble(partes[2].trim());
                        double lon = Double.parseDouble(partes[3].trim());
                        ciudades.add(new Ciudad(id, nombre, lat, lon));
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(null, "Error en la línea " + lineaNum + ": " + ex.getMessage());
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Formato incorrecto en la línea " + lineaNum);
                }
                lineaNum++;
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al leer el archivo: " + e.getMessage());
        }
        return ciudades;
    }

    public static void main(String[] args) {
        List<Ciudad> ciudades = null;

        // Pregunta al usuario si desea cargar las ciudades desde un archivo externo
        int opcion = JOptionPane.showConfirmDialog(null, "¿Deseas cargar las ciudades desde un archivo?", "Cargar ciudades", JOptionPane.YES_NO_OPTION);
        if (opcion == JOptionPane.YES_OPTION) {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Selecciona el archivo de ciudades");
            if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                File archivo = fileChooser.getSelectedFile();
                ciudades = cargarCiudadesDesdeArchivo(archivo);
            }
        }

        // Si no se cargó archivo o está vacío, usa las ciudades por defecto
        if (ciudades == null || ciudades.isEmpty()) {
            ciudades = new ArrayList<>();
            ciudades.add(new Ciudad(0, "Ciudad de México", 19.4326, -99.1332));
            ciudades.add(new Ciudad(1, "Guadalajara", 20.6597, -103.3496));
            ciudades.add(new Ciudad(2, "Monterrey", 25.6866, -100.3161));
            ciudades.add(new Ciudad(3, "Puebla", 19.0414, -98.2063));
            ciudades.add(new Ciudad(4, "Tijuana", 32.5149, -117.0382));
            ciudades.add(new Ciudad(5, "Mérida", 20.9674, -89.5926));
            ciudades.add(new Ciudad(6, "León", 21.1619, -101.6860));
            ciudades.add(new Ciudad(7, "Querétaro", 20.5888, -100.3899));
        }

        // Advierte si hay muchas ciudades (el algoritmo puede tardar mucho)
        if (ciudades.size() > 10) {
            JOptionPane.showMessageDialog(null,
                "Advertencia: El número de ciudades es alto y el algoritmo puede tardar mucho tiempo.",
                "Advertencia", JOptionPane.WARNING_MESSAGE);
        }

        // Valida que haya al menos 3 ciudades para ejecutar el algoritmo y graficar
        if (ciudades.size() < 3) {
            JOptionPane.showMessageDialog(null, "Se requieren al menos 3 ciudades para ejecutar el algoritmo y mostrar gráficas.");
            return;
        }

        // Crea la matriz de distancias entre ciudades usando la fórmula de Haversine
        int n = ciudades.size();
        double[][] distancias = new double[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                distancias[i][j] = (i == j) ? 0 : ciudades.get(i).distanciaA(ciudades.get(j));
            }
        }

        // Crea el grafo con las ciudades y sus distancias
        Grafo grafo = new Grafo(ciudades, distancias);

        // Medición de memoria antes de ejecutar el algoritmo
        Runtime runtime = Runtime.getRuntime();
        runtime.gc(); // Sugerir recolección de basura
        long memoriaAntes = runtime.totalMemory() - runtime.freeMemory();

        // Medición de tiempo de ejecución
        long inicio = System.currentTimeMillis();

        // Ejecuta el algoritmo de fuerza bruta para TSP
        FuerzaBrutaTSP tsp = new FuerzaBrutaTSP(grafo);
        tsp.resolver();
       
            // Después de resolver el TSP y antes o después de mostrar la ventana:
            System.out.println("Recorrido óptimo:");
            for (Ciudad c : tsp.getMejorRecorrido()) {
                System.out.print(c.getNombre() + " -> ");
            }
            System.out.println(tsp.getMejorRecorrido().get(0).getNombre()); // Regreso al inicio
            

        long fin = System.currentTimeMillis();
        double tiempoSegundos = (fin - inicio) / 1000.0;

        // Medición de memoria después de ejecutar el algoritmo
        long memoriaDespues = runtime.totalMemory() - runtime.freeMemory();
        double memoriaConsumidaMB = (memoriaDespues - memoriaAntes) / (1024.0 * 1024.0);

        // Visualiza el recorrido óptimo usando Swing con botones
        javax.swing.SwingUtilities.invokeLater(() -> {
            VisualizacionTSP.mostrarRecorrido(
                tsp.getMejorRecorrido(),
                tsp.getMejorDistancia(),
                tsp.getRutasEvaluadas(),
                tiempoSegundos,
                memoriaConsumidaMB
            );
        });

        // --- Gráfica de crecimiento exponencial ---
        int maxCiudades = Math.min(10, ciudades.size());
        if (maxCiudades < 3) {
            JOptionPane.showMessageDialog(null, "No hay suficientes ciudades para mostrar gráficas de crecimiento.");
            return;
        }
        List<Integer> listaCiudades = new ArrayList<>();
        List<Long> listaRutas = new ArrayList<>();
        List<Double> listaTiempos = new ArrayList<>();

        // Ejecuta el algoritmo para diferentes cantidades de ciudades y guarda los resultados
        for (int num = 3; num <= maxCiudades; num++) {
            List<Ciudad> subLista = ciudades.subList(0, num);
            int n2 = subLista.size();
            double[][] distancias2 = new double[n2][n2];
            for (int i = 0; i < n2; i++)
                for (int j = 0; j < n2; j++) {
                    distancias2[i][j] = (i == j) ? 0 : subLista.get(i).distanciaA(subLista.get(j));
                }
            Grafo grafoTemp = new Grafo(subLista, distancias2);

            Runtime runtime2 = Runtime.getRuntime();
            runtime2.gc();
            long inicio2 = System.currentTimeMillis();

            FuerzaBrutaTSP tspTemp = new FuerzaBrutaTSP(grafoTemp);
            tspTemp.resolver();

            long fin2 = System.currentTimeMillis();
            double tiempoSegundos2 = (fin2 - inicio2) / 1000.0;

            listaCiudades.add(num);
            listaRutas.add(tspTemp.getRutasEvaluadas());
            listaTiempos.add(tiempoSegundos2);
        }

        // Muestra la gráfica SOLO de rutas evaluadas
        javax.swing.SwingUtilities.invokeLater(() -> {
            GraficaSimple.mostrarGrafica(
                listaCiudades,
                listaRutas,
                "Crecimiento de Rutas Evaluadas (TSP Fuerza Bruta)",
                "Número de ciudades",
                "Rutas evaluadas",
                Color.RED
            );
        });

        // Muestra la gráfica SOLO de tiempo de ejecución
        javax.swing.SwingUtilities.invokeLater(() -> {
            GraficaSimple.mostrarGrafica(
                listaCiudades,
                listaTiempos,
                "Crecimiento del Tiempo de Ejecución (TSP Fuerza Bruta)",
                "Número de ciudades",
                "Tiempo (s)",
                Color.BLUE
            );
        });
    }
}


