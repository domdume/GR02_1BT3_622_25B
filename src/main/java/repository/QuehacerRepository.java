package repository;

import model.Quehacer;
import model.MiembroHogar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuehacerRepository extends JpaRepository<Quehacer, Long> {
    
    List<Quehacer> findByMiembroHogar(MiembroHogar miembro);
    
    List<Quehacer> findByEstadoCompletado(boolean completado);
    
    List<Quehacer> findByMiembroHogarId(Long miembroId);
}
