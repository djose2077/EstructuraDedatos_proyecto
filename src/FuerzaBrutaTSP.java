import java.util.*;

public class FuerzaBrutaTSP {
    private Grafo grafo;
    private List<Ciudad> mejorRecorrido;
    private double mejorDistancia = Double.MAX_VALUE;
    private long rutasEvaluadas = 0; // Contador de rutas evaluadas

    public FuerzaBrutaTSP(Grafo grafo) {
        this.grafo = grafo;
    }

    public void resolver() {
        List<Ciudad> ciudades = grafo.getCiudades();
        List<Ciudad> recorridoActual = new ArrayList<>(ciudades);
        permutar(recorridoActual, 1);
    }

    // Algoritmo de permutación para probar todas las rutas posibles (fuerza bruta)
    private void permutar(List<Ciudad> recorrido, int inicio) {
        if (inicio == recorrido.size() - 1) {
            rutasEvaluadas++; // Incrementar contador
            double distancia = calcularDistancia(recorrido);
            if (distancia < mejorDistancia) {
                mejorDistancia = distancia;
                mejorRecorrido = new ArrayList<>(recorrido);
            }
        } else {
            for (int i = inicio; i < recorrido.size(); i++) {
                Collections.swap(recorrido, inicio, i);
                permutar(recorrido, inicio + 1);
                Collections.swap(recorrido, inicio, i);
            }
        }
    }

    // Calcula la distancia total de un recorrido (incluyendo el regreso al origen)
    private double calcularDistancia(List<Ciudad> recorrido) {
        double suma = 0;
        for (int i = 0; i < recorrido.size() - 1; i++) {
            suma += grafo.getDistancia(recorrido.get(i).getId(), recorrido.get(i + 1).getId());
        }
        suma += grafo.getDistancia(recorrido.get(recorrido.size() - 1).getId(), recorrido.get(0).getId());
        return suma;
    }

    public List<Ciudad> getMejorRecorrido() {
        return mejorRecorrido;
    }

    public double getMejorDistancia() {
        return mejorDistancia;
    }

    public long getRutasEvaluadas() {
        return rutasEvaluadas;
    }
}
