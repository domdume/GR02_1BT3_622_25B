package service;

import model.Incentivo;
import model.MiembroHogar;
import model.Quehacer;
import model.TipoIncentivo;
import repository.IncentivoRepository;
import repository.MiembroHogarRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class IncentivoService {
    
    @Autowired
    private IncentivoRepository incentivoRepository;
    
    @Autowired
    private MiembroHogarRepository miembroRepository;
    
    public void aplicarIncentivo(MiembroHogar miembro, Quehacer quehacer) {
        if (miembro == null || quehacer == null) {
            throw new IllegalArgumentException("Miembro y quehacer no pueden ser nulos");
        }
        
        TipoIncentivo tipo;
        int puntos;
        String descripcion;
        
        if (quehacer.fueCompletadoATiempo()) {
            tipo = TipoIncentivo.RECOMPENSA;
            puntos = calcularPuntosRecompensa(quehacer);
            descripcion = "Quehacer completado a tiempo";
        } else {
            tipo = TipoIncentivo.PENALIZACION;
            puntos = Incentivo.PENALIZACION;
            descripcion = "Quehacer no completado a tiempo";
        }
        
        Incentivo incentivo = new Incentivo(tipo, puntos, descripcion, miembro, quehacer);
        incentivoRepository.save(incentivo);
        
        // Actualizar puntos del miembro
        if (tipo == TipoIncentivo.RECOMPENSA) {
            miembro.setPuntos(miembro.getPuntos() + puntos);
        } else {
            miembro.setPuntos(Math.max(0, miembro.getPuntos() - puntos));
        }
        miembroRepository.save(miembro);
    }
    
    private int calcularPuntosRecompensa(Quehacer quehacer) {
        if (quehacer.getDificultad() == null) {
            return Incentivo.PUNTOS_FACIL;
        }
        switch (quehacer.getDificultad()) {
            case FACIL: return Incentivo.PUNTOS_FACIL;
            case MEDIO: return Incentivo.PUNTOS_MEDIO;
            case DIFICIL: return Incentivo.PUNTOS_DIFICIL;
            default: return Incentivo.PUNTOS_FACIL;
        }
    }
    
    public List<Incentivo> obtenerPorMiembro(MiembroHogar miembro) {
        return incentivoRepository.findByMiembroHogar(miembro);
    }
    
    public List<Incentivo> obtenerTodos() {
        return incentivoRepository.findAll();
    }
}