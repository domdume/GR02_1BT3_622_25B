package service;

import model.MiembroHogar;
import repository.MiembroHogarRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class MiembroHogarService {
    
    @Autowired
    private MiembroHogarRepository repository;
    
    public void crearMiembro(MiembroHogar miembro) {
        if (miembro.getNombre() == null || miembro.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del miembro no puede estar vacío");
        }
        if (miembro.getEdad() <= 0) {
            throw new IllegalArgumentException("La edad debe ser mayor a 0");
        }
        
        repository.save(miembro);
    }
    
    public MiembroHogar obtenerPorId(Long id) {
        return repository.findByIdWithQuehaceres(id).orElse(null);
    }
    
    public List<MiembroHogar> obtenerTodos() {
        return repository.findAllWithQuehaceres();
    }
    
    public void actualizar(MiembroHogar miembro) {
        repository.save(miembro);
    }
    
    public void eliminar(Long id) {

        repository.deleteById(id);
    }
}
