package model;

public enum Liga {
    BRONCE(1, 0),
    PLATA(2, 500),
    ORO(3, 1500);

    private final int nivel;
    private final int puntosRequeridos;

    Liga(int nivel, int puntosRequeridos) {
        this.nivel = nivel;
        this.puntosRequeridos = puntosRequeridos;
    }

    public int getNivel() {
        return nivel;
    }

    public int getPuntosRequeridos() {
        return puntosRequeridos;
    }

    public static Liga determinarPorPuntos(int puntos) {
        if (puntos >= ORO.puntosRequeridos) return ORO;
        if (puntos >= PLATA.puntosRequeridos) return PLATA;
        return BRONCE;
    }
}
