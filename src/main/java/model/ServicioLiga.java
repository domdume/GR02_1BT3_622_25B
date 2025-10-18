package model;

public class ServicioLiga {
    private static final int PLATA_UMBRAL = 500;
    private static final int ORO_UMBRAL = 1500;

    public void actualizarPuntos(MiembroHogar miembro, int puntos) {
        miembro.setPuntos(miembro.getPuntos() + puntos);
        if (miembro.getPuntos() >= ORO_UMBRAL) {
            miembro.setLiga(Liga.ORO);
        } else if (miembro.getPuntos() >= PLATA_UMBRAL) {
            miembro.setLiga(Liga.PLATA);
        }
    }

    public void removerPuntos(MiembroHogar miembro, int puntosToRemove) {
        int nuevosPuntos = miembro.getPuntos() - puntosToRemove;
        if (nuevosPuntos < 0) {
            nuevosPuntos = 0; // Evitar puntos negativos
        }
        miembro.setPuntos(nuevosPuntos);
        if (miembro.getPuntos() < PLATA_UMBRAL) {
            miembro.setLiga(Liga.BRONCE);
        } else if (miembro.getPuntos() < ORO_UMBRAL) {
            miembro.setLiga(Liga.PLATA);
    }
}
}
