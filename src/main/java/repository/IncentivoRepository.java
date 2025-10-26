package repository;

import model.Incentivo;
import model.MiembroHogar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IncentivoRepository extends JpaRepository<Incentivo, Long> {
    
    List<Incentivo> findByMiembroHogar(MiembroHogar miembro);
    
    List<Incentivo> findByMiembroHogarId(Long miembroId);
}
