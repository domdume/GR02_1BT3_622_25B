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
    
    public List<MiembroHogar> getMiembros() {
        return miembrosConObligacion;
    }
    
    public MiembroHogar buscarMiembroPorNombre(String nombre) {
        for (MiembroHogar miembro : miembrosConObligacion) {
            if (miembro.getNombre().equalsIgnoreCase(nombre.trim())) {
                return miembro;
            }
        }
        return null;
    }
}