package model;


import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class MiembroHogar implements Observador{
    private String nombre;
    private int edad;
    private List<Quehacer> quehaceres;
    private List<Incentivo> incentivos;
    private List<Incentivo> penalizaciones;
    private int factorDeCarga;

    public MiembroHogar(String nombre, int edad){
        this.nombre = nombre;
        this.edad = edad;
        this.quehaceres = new ArrayList<>();
        this.incentivos = new ArrayList<>();
        this.penalizaciones = new ArrayList<>();
        this.factorDeCarga = 0;
    }
    @Override
    public void actualizar(String mensaje) {
        System.out.println("[Notificación para " + nombre + "]: " + mensaje);
    }

    public void registrarQuehacerCompleto(Quehacer quehacer) {
        if (!this.quehaceres.contains(quehacer)) {
            System.out.println("AVISO: " + nombre + " no puede completar una tarea que no tiene asignada.");
            return;
        }
        // 1. El miembro actualiza el estado de la tarea y su propia lista.
        quehacer.marcarComoCompletado();
        this.quehaceres.remove(quehacer);
        // 2. Llama al experto para que aplique la lógica de incentivos/penalizaciones.
        Incentivo.aplicar(this, quehacer);
    }
    public void reducirFactorDeCarga() { this.factorDeCarga--; }
    public void aumentarFactorDeCarga() { this.factorDeCarga++; }
    public void removerQuehacer(Quehacer q) { this.quehaceres.remove(q); }
    public int getFactorDeCarga() { return this.factorDeCarga; }
    public String getNombre() {
        return nombre;
    }
    protected int getEdad() {
        return edad;
    }
    public void asignarQuehacer(Quehacer q) {
        this.quehaceres.add(q); }
    public List<Quehacer> getQuehaceresAsignados() { return quehaceres; }
}

