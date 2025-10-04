package model;

import java.util.ArrayList;
import java.util.List;

public class MiembroHogar implements Observador{
    private String nombre;
    private int edad;
    private List<Quehacer> quehaceres;
    private List<Incentivo> incentivos;
    private List<Incentivo> penalizaciones;
    public MiembroHogar(String nombre, int edad){
        this.nombre = nombre;
        this.edad = edad;
        this.quehaceres = new ArrayList<>();
        this.incentivos = new ArrayList<>();
        this.penalizaciones = new ArrayList<>();
    }
    @Override
    public void actualizar(String mensaje) {
        System.out.println("[Notificación para " + nombre + "]: " + mensaje);
    }

    public String getNombre() {
        return nombre;
    }

    protected int getEdad() {
        return edad;
    }
}