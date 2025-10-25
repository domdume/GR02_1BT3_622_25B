package model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidad que representa un miembro del hogar.
 * Implementa el patrón Observer para recibir notificaciones.
 * Clase base para la jerarquía de miembros (JefeDelHogar hereda de esta).
 */
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "DTYPE", discriminatorType = DiscriminatorType.STRING)
@DiscriminatorValue("MiembroHogar")
public class MiembroHogar implements Observador {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private int edad;
    private int puntos;
    private Liga liga; // Added for rewards

    @OneToMany(mappedBy = "miembroHogar", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Quehacer> quehaceres;

    @OneToMany(mappedBy = "miembroHogar", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Incentivo> incentivos;

    @Transient // Temporalmente como @Transient para evitar problemas de BD
    private int factorDeCarga; // Campo requerido según diagrama UML

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
        this.puntos = 0; // Initialize points
        this.factorDeCarga = 0;
        this.liga = Liga.BRONCE;
    }

    // Getters y Setters para JPA y JSP
    public Long getId() {
        return id;
    }
    public Liga getLiga() {
        return liga;
    }
    public void setLiga(Liga liga) {
        this.liga = liga;
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

    public void anadirIncentivo(Incentivo incentivo) {
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
//    public void reducirFactorDeCarga() { this.factorDeCarga--; }
//    public void aumentarFactorDeCarga() { this.factorDeCarga++; }
//    public void removerQuehacer(Quehacer q) { this.quehaceres.remove(q); }
//    public int getFactorDeCarga() { return this.factorDeCarga; }
    public String getNombre() {
        return nombre;
    }
    public int getEdad() { // Cambiado a public para acceso desde JSP
        return edad;
    }
    public void asignarQuehacer(Quehacer q) {
        if (q == null) return;
        // Set owning side so JPA persist the relationship
        q.setMiembroHogar(this);
        if (!this.quehaceres.contains(q)) {
            this.quehaceres.add(q);
        }
    }

    public int getPuntos() {
        return puntos;
    }

    public void setPuntos(int puntos) {
        this.puntos = puntos;
    }

//    // Método según diagrama UML
//    public void realizarQuehacer(Quehacer q) {
//        if (this.quehaceres.contains(q)) {
//            q.marcarCompletado();
//            this.quehaceres.remove(q);
//            System.out.println(this.nombre + " ha realizado el quehacer: " + q.getNombre());
//        } else {
//            System.out.println("AVISO: " + this.nombre + " no puede realizar una tarea que no tiene asignada.");
//        }
//    }



}
