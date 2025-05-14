import java.util.List;

public class Grafo {
    private List<Ciudad> ciudades;
    private double[][] distancias;

    public Grafo(List<Ciudad> ciudades, double[][] distancias) {
        this.ciudades = ciudades;
        this.distancias = distancias;
    }

    public List<Ciudad> getCiudades() {
        return ciudades;
    }

    public double getDistancia(int i, int j) {
        return distancias[i][j];
    }

    public int getNumeroCiudades() {
        return ciudades.size();
    }
}
