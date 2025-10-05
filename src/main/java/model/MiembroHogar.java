package model;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

public class MiembroHogar implements Observador{
    private String nombre;
    private int edad;
    private List<Quehacer> quehaceres;
    private List<Incentivo> incentivos;
    private int factorDeCarga;

    public MiembroHogar(String nombre, int edad){
        this.nombre = nombre;
        this.edad = edad;
        this.quehaceres = new ArrayList<>();
        this.incentivos = new ArrayList<>();
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
    protected int getEdad() {
        return edad;
    }
    public void asignarQuehacer(Quehacer q) {
        this.quehaceres.add(q); }
    public List<Quehacer> getQuehaceresAsignados() { return quehaceres; }

    @Override
    public String toString() {
        return "MiembroHogar{" +
                "nombre='" + nombre + '\'' +
                ", edad=" + edad +
                ", quehaceres=" + quehaceres +
                ", incentivos=" + incentivos +

                ", factorDeCarga=" + factorDeCarga +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;  // 1️⃣ Mismo objeto en memoria
        if (!(o instanceof MiembroHogar)) return false;  // 2️⃣ Verifica que sea de la misma clase
        MiembroHogar otro = (MiembroHogar) o;  // 3️⃣ Castea

        // 4️⃣ Compara los atributos que definen la identidad de un miembro
        return this.nombre.equals(otro.nombre) && this.edad == otro.edad;
    }

    @Override
    public int hashCode() {
        // Siempre que sobrescribes equals(), también debes sobrescribir hashCode()
        return java.util.Objects.hash(nombre, edad);
    }

    public void setIncentivo(Incentivo incentivo) {
        this.incentivos.add(incentivo);
    }

    public List<Incentivo> getIncentivos() {
        return this.incentivos;
    }
}

