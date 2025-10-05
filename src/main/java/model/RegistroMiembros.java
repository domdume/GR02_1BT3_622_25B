package model;

import java.util.ArrayList;
import java.util.List;

public class RegistroMiembros {
    private List<MiembroHogar> miembrosConObligacion;
    public RegistroMiembros() {
        this.miembrosConObligacion = new ArrayList<>();
    }
    public void agregarMiembroConObligacion(MiembroHogar miembroHogar) {
        miembrosConObligacion.add(miembroHogar);
        System.out.println("LOG: " + miembroHogar.getNombre() + " ha sido añadido al registro de miembros.");
    }
}
