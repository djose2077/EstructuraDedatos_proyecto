public class Ciudad {
    private String nombre;
    private int id;
    private double latitud;
    private double longitud;

    public Ciudad(int id, String nombre, double latitud, double longitud) {
        this.id = id;
        this.nombre = nombre;
        this.latitud = latitud;
        this.longitud = longitud;
    }

    public String getNombre() {
        return nombre;
    }

    public int getId() {
        return id;
    }

    public double getLatitud() {
        return latitud;
    }

    public double getLongitud() {
        return longitud;
    }

    // Calcula la distancia a otra ciudad usando la fórmula de Haversine
    public double distanciaA(Ciudad otra) {
        final int R = 6371; // Radio de la Tierra en km
        double latDist = Math.toRadians(otra.latitud - this.latitud);
        double lonDist = Math.toRadians(otra.longitud - this.longitud);
        double a = Math.sin(latDist / 2) * Math.sin(latDist / 2)
                + Math.cos(Math.toRadians(this.latitud)) * Math.cos(Math.toRadians(otra.latitud))
                * Math.sin(lonDist / 2) * Math.sin(lonDist / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }

    @Override
    public String toString() {
        return nombre;
    }
}
