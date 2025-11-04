package model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "miembro_hogar")
public class MiembroHogar {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private int edad;

    @Column(name = "tareas_completadas")
    private int tareasCompletadas = 0;

    @Column(name = "racha_congelada")
    private boolean rachaCongelada = false;

    @Column(name = "puntos")
    private int puntos = 0;

    @Enumerated(EnumType.STRING)
    @Column(name = "liga")
    private Liga liga = Liga.BRONCE; // Inicializado en BRONCE por defecto

    @OneToMany(mappedBy = "miembro", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Logro> logros = new HashSet<>();

    @OneToMany(mappedBy = "miembroHogar", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Quehacer> quehaceres = new HashSet<>();

    // Constructor por defecto requerido por JPA
    public MiembroHogar() {
    }

    public MiembroHogar(String nombre, int edad) {
        this.nombre = nombre;
        this.edad = edad;
        this.rachaCongelada = false;
        this.puntos = 0;
        this.liga = Liga.BRONCE;
    }

    // Getters y setters básicos
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

    public boolean getRachaCongelada() {
        return rachaCongelada;
    }

    public void setRachaCongelada(boolean rachaCongelada) {
        this.rachaCongelada = rachaCongelada;
    }

    public int getTareasCompletadas() {
        return tareasCompletadas;
    }

    public void setTareasCompletadas(int tareasCompletadas) {
        this.tareasCompletadas = tareasCompletadas;
    }

    public int getPuntos() {
        return puntos;
    }

    public void setPuntos(int puntos) {
        this.puntos = puntos;
    }

    public Liga getLiga() {
        return liga;
    }

    public void setLiga(Liga liga) {
        this.liga = liga;
    }

    public Set<Logro> getLogros() {
        return logros;
    }

    public void setLogros(Set<Logro> logros) {
        this.logros = logros;
    }

    public Set<Quehacer> getQuehaceres() {
        return new HashSet<>(quehaceres);
    }

    public void setQuehaceres(Set<Quehacer> quehaceres) {
        this.quehaceres = new HashSet<>(quehaceres);
    }

    // Métodos de gestión de logros
    public void agregarLogro(Logro logro) {
        logros.add(logro);
        logro.setMiembro(this);
    }

    public void removerLogro(Logro logro) {
        logros.remove(logro);
        logro.setMiembro(null);
    }

    public void registrarQuehacerCompleto(Quehacer quehacer) {
        if (quehacer == null) {
            throw new IllegalArgumentException("El quehacer no puede ser nulo");
        }

        // Verificar que el quehacer pertenezca a este miembro
        if (!this.equals(quehacer.getMiembroHogar())) {
            throw new IllegalStateException("El quehacer no pertenece a este miembro");
        }

        // Actualizar el estado de la tarea
        quehacer.setFechaFinalizacion(LocalDateTime.now());
        quehacer.setEstado(EstadoQuehacer.COMPLETADO);

        // Actualizar contador de tareas y puntos
        this.tareasCompletadas++;
        this.puntos += calcularPuntosPorDificultad(quehacer.getDificultad());

        // Remover la tarea de la lista de quehaceres pendientes
        this.quehaceres.remove(quehacer);
    }

    private int calcularPuntosPorDificultad(Dificultad dificultad) {
        if (dificultad == null) return 1;

        switch (dificultad) {
            case FACIL:
                return 1;
            case MEDIO:
                return 2;
            case DIFICIL:
                return 3;
            default:
                return 1;
        }
    }

    public void agregarQuehacer(Quehacer quehacer) {
        if (quehacer != null) {
            quehaceres.add(quehacer);
            quehacer.setMiembroHogar(this);
        }
    }

    public void removerQuehacer(Quehacer quehacer) {
        if (quehacer != null) {
            quehaceres.remove(quehacer);
            quehacer.setMiembroHogar(null);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MiembroHogar that = (MiembroHogar) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 31;
    }
}
