package model;

public class Logro {
    private String id;
    private TipoLogro tipoLogro;
    private TipoMedalla nivel;
    private int tareasRequeridas;

    public Logro(String id, TipoLogro tipoLogro, TipoMedalla nivel, int tareasRequeridas) {
        this.id = id;
        this.tipoLogro = tipoLogro;
        this.nivel = nivel;
        this.tareasRequeridas = tareasRequeridas;
    }

    public String getId() {
        return id;
    }

    public TipoLogro getTipoLogro() {
        return tipoLogro;
    }

    public TipoMedalla getNivel() {
        return nivel;
    }

    public int getTareasRequeridas() {
        return tareasRequeridas;
    }

    public boolean seCumpleConTareas(int tareasCompletadas) {
        return tareasCompletadas >= tareasRequeridas;
    }
}
