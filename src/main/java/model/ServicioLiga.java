package model;

public class ServicioLiga {

    private static final int PLATA_UMBRAL = 500;
    private static final int ORO_UMBRAL = 1500;

    public void actualizarPuntos(MiembroHogar miembro, int puntos) {
        // 1. Actualiza los puntos
        miembro.setPuntos(miembro.getPuntos() + puntos);

        // 2. Verifica y actualiza la liga (El IF/ELSE IF garantiza el ascenso correcto)
        if (miembro.getPuntos() >= ORO_UMBRAL) {
            miembro.setLiga(Liga.ORO);
        } else if (miembro.getPuntos() >= PLATA_UMBRAL) {
            miembro.setLiga(Liga.PLATA);
        }
    }
}
