package model;


import java.util.ArrayList;
import java.util.List;

public class RegistroQuehacer implements Sujeto {

    // Atributos
    private List<Quehacer> quehaceresPorRealizar;
    private List<Observador> observadores; // Lista de suscriptores

    // Constructor
    public RegistroQuehacer() {
        this.quehaceresPorRealizar = new ArrayList<>();
        this.observadores = new ArrayList<>();
    }

    // --- Métodos de Gestión del Patrón Observer (Suscripción/Notificación) ---

    @Override
    public void suscribir(Observador observador) {
        this.observadores.add(observador);
        System.out.println("SUSCRITO!");
    }

    @Override
    public void desuscribir(Observador observador) {
        this.observadores.remove(observador);
        System.out.println("DESUSCRITO!");
    }

    @Override
    public void notificar(String mensaje) {
        for (Observador observador : observadores) {
            observador.actualizar(mensaje);
            System.out.println("NOTIFICADO");
        }
    }
    // --- Métodos de Lógica de Negocio (El cambio de estado) ---

    /**
     * Método: agregarQuehacer(q: Quehacer)
     * Agrega un nuevo quehacer y notifica a todos los observadores.
     */
    public void agregarQuehacer(Quehacer q) {
        this.quehaceresPorRealizar.add(q);
        String mensaje = "¡Nuevo quehacer añadido!: " + q.getNombre() ;
        System.out.println("✅ " + mensaje);

        // El cambio de estado ocurre, por lo que notificamos a los suscriptores.
        notificar(mensaje);
    }
}
