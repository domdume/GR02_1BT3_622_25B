package model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import model.Logro;
import model.TipoLogro;

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
    /**
     * Indica si el miembro es jefe del hogar (para JSP).
     */
    public boolean getEsJefe() {
        return false;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private int edad;
    private int puntos;
    @Enumerated(EnumType.STRING)
    private Liga liga; // Added for rewards

    @OneToMany(mappedBy = "miembroHogar", cascade = CascadeType.ALL, orphanRemoval = true)
    private java.util.Set<Quehacer> quehaceres;

    @OneToMany(mappedBy = "miembroHogar", cascade = CascadeType.ALL, orphanRemoval = true)
    private java.util.Set<Incentivo> incentivos;

    @OneToMany(mappedBy = "miembro", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Logro> logros = new ArrayList<>();

    // Contador de tareas completadas (usado por LogroService/repository)
    private int tareasCompletadas = 0;

    private int factorDeCarga; // Campo requerido según diagrama UML

    // NUEVO: flag para proteger la racha (congelamiento)
    @Column(name = "racha_congelada", nullable = false)
    private boolean rachaCongelada = false;

    // Constructor vacío requerido por JPA
    public MiembroHogar() {
        this.quehaceres = new java.util.HashSet<>();
        this.incentivos = new java.util.HashSet<>();
        // Inicializar a no congelado por defecto
        this.rachaCongelada = false;
    }

    public MiembroHogar(String nombre, int edad) {
        this.nombre = nombre;
        this.edad = edad;
        this.quehaceres = new java.util.HashSet<>();
        this.incentivos = new java.util.HashSet<>();
        this.puntos = 0; // Initialize points
        this.factorDeCarga = 0;
        this.liga = Liga.BRONCE;
        // Inicializar a no congelado por defecto
        this.rachaCongelada = false;
        // Asignar emblema de Bronce por defecto a todos los miembros nuevos
        try {
            // Asignar la primera insignia (emblema de bronce) con el código esperado por la lógica de negocio
            Logro emblemaBronce = new Logro("EMBLEMA_APRENDIZ_CONSTANTE", TipoLogro.EMBLEMA, 0);
            this.addLogro(emblemaBronce);
        } catch (Exception ex) {
            // No detener la creación por un fallo en la asignación de logro
            System.out.println("[MiembroHogar] No se pudo asignar emblema por defecto: " + ex.getMessage());
        }
    }

    // Getters y Setters para JPA y JSP
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getNombre() {
        return nombre;
    }
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    public int getEdad() {
        return edad;
    }
    public void setEdad(int edad) {
        this.edad = edad;
    }

    // NUEVO: acceso al estado de congelamiento para servicios/JSP
    public boolean isRachaCongelada() { return rachaCongelada; }
    public boolean getRachaCongelada() { return rachaCongelada; }
    public void setRachaCongelada(boolean rachaCongelada) { this.rachaCongelada = rachaCongelada; }

    public Liga getLiga() {
        return liga;
    }
    public void setLiga(Liga liga) {
        this.liga = liga;
    }

    public List<Quehacer> getQuehaceres() {
        return new java.util.ArrayList<>(quehaceres);
    }

    public void setQuehaceres(List<Quehacer> quehaceres) {
        this.quehaceres = new java.util.HashSet<>(quehaceres);
    }

    public List<Incentivo> getIncentivos() {
        return new java.util.ArrayList<>(incentivos);
    }

    public void setIncentivos(List<Incentivo> incentivos) {
        this.incentivos = new java.util.HashSet<>(incentivos);
    }

    public List<Logro> getLogros() {
        return new ArrayList<>(logros);
    }

    public void addLogro(Logro logro) {
        if (logro == null) return;
        this.logros.add(logro);
        logro.setMiembro(this);
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
        if (quehacer == null) {
            throw new IllegalArgumentException("El quehacer no puede ser nulo");
        }
        if (!this.quehaceres.contains(quehacer)) {
            System.out.println("AVISO: " + nombre + " no puede completar una tarea que no tiene asignada.");
            return;
        }
        // El miembro actualiza el estado de la tarea y su propia lista.
        quehacer.setFechaFinalizacion(LocalDateTime.now());
        quehacer.setEstado(EstadoQuehacer.COMPLETADO);
        this.quehaceres.remove(quehacer);
        // Aplicar incentivo solo desde el service (que persiste en BD)
        Incentivo.aplicarIncentivo(this, quehacer);
    }
//    public void reducirFactorDeCarga() { this.factorDeCarga--; }
//    public void aumentarFactorDeCarga() { this.factorDeCarga++; }
//    public void removerQuehacer(Quehacer q) { this.quehaceres.remove(q); }
//    public int getFactorDeCarga() { return this.factorDeCarga; }


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

    public int getTareasCompletadas() {
        return tareasCompletadas;
    }

    public void setTareasCompletadas(int tareasCompletadas) {
        this.tareasCompletadas = tareasCompletadas;
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