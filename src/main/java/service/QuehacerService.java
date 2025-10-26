package service;

import model.Quehacer;
import model.MiembroHogar;
import repository.QuehacerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class QuehacerService {
    
    @Autowired
    private QuehacerRepository repository;
    
    public void crear(Quehacer quehacer) {
        if (quehacer.getNombre() == null || quehacer.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del quehacer no puede estar vacío");
        }
        repository.save(quehacer);
    }
    
    public Quehacer obtenerPorId(Long id) {
        return repository.findById(id).orElse(null);
    }
    
    public List<Quehacer> obtenerTodos() {
        return repository.findAll();
    }
    
    public List<Quehacer> obtenerPorMiembro(MiembroHogar miembro) {
        return repository.findByMiembroHogar(miembro);
    }
    
    public List<Quehacer> obtenerPorEstado(boolean completado) {
        return repository.findByEstadoCompletado(completado);
    }
    
    public void actualizar(Quehacer quehacer) {
        repository.save(quehacer);
    }
    
    public void eliminar(Long id) {
        repository.deleteById(id);
    }
}
