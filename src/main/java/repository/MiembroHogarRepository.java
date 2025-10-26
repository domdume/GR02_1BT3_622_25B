package repository;

import model.MiembroHogar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MiembroHogarRepository extends JpaRepository<MiembroHogar, Long> {
    
    @Query("SELECT m FROM MiembroHogar m LEFT JOIN FETCH m.quehaceres WHERE m.id = :id")
    Optional<MiembroHogar> findByIdWithQuehaceres(@Param("id") Long id);
    
    @Query("SELECT DISTINCT m FROM MiembroHogar m LEFT JOIN FETCH m.quehaceres")
    List<MiembroHogar> findAllWithQuehaceres();
}
