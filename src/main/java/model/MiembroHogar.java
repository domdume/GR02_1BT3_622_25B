package model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class MiembroHogar implements Observador {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private int edad;

    @OneToMany(mappedBy = "miembroHogar", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Quehacer> quehaceres;

    @OneToMany(mappedBy = "miembroHogar", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Incentivo> incentivos;

    @Transient // Ignorado por JPA por ahora
    private int factorDeCarga;

    // Constructor vacío requerido por JPA
    public MiembroHogar() {
        this.quehaceres = new ArrayList<>();
        this.incentivos = new ArrayList<>();
    }

    public MiembroHogar(String nombre, int edad) {
        this.nombre = nombre;
        this.edad = edad;
        this.quehaceres = new ArrayList<>();
        this.incentivos = new ArrayList<>();
        this.factorDeCarga = 0;
    }

    // Getters y Setters para JPA y JSP
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setEdad(int edad) {
        this.edad = edad;
    }

    public List<Quehacer> getQuehaceres() {
        return quehaceres;
    }

    public void setQuehaceres(List<Quehacer> quehaceres) {
        this.quehaceres = quehaceres;
    }

    public List<Incentivo> getIncentivos() {
        return incentivos;
    }

    public void setIncentivos(List<Incentivo> incentivos) {
        this.incentivos = incentivos;
    }

    public void addIncentivo(Incentivo incentivo) {
        this.incentivos.add(incentivo);
        incentivo.setMiembroHogar(this);
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
        Incentivo incentivo = new Incentivo();
        incentivo.aplicar(this, quehacer);
    }
    public void reducirFactorDeCarga() { this.factorDeCarga--; }
    public void aumentarFactorDeCarga() { this.factorDeCarga++; }
    public void removerQuehacer(Quehacer q) { this.quehaceres.remove(q); }
    public int getFactorDeCarga() { return this.factorDeCarga; }
    public String getNombre() {
        return nombre;
    }
    public int getEdad() { // Cambiado a public para acceso desde JSP
        return edad;
    }
    public void asignarQuehacer(Quehacer q) {
        if (!this.quehaceres.contains(q)) {
            this.quehaceres.add(q);
        }
    }
}

