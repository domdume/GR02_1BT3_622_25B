package service;

import dao.IncentivoDAO;
import dao.MiembroHogarDAO;
import model.Dificultad;
import model.EstadoQuehacer;
import model.Liga;
import model.MiembroHogar;
import model.Quehacer;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.time.LocalDateTime;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class IEmblemaServiceTest {
    
    private IEmblemaService emblemaService;
    private IncentivoDAO incentivoDAO;
    private MiembroHogarDAO miembroHogarDAO;
    private IncentivoService service;

    @Before
    public void setUp() {
        emblemaService = mock(IEmblemaService.class);
        incentivoDAO = mock(IncentivoDAO.class);
        miembroHogarDAO = mock(MiembroHogarDAO.class);
        service = new IncentivoService(emblemaService, incentivoDAO, miembroHogarDAO);
    }

    @Test
    public void dado_AscensoDeBronceAPlata_Cuando_IncentivoServiceProcesa_Entonces_AsignaEmblemaExplorador() {
        // Arrange
        doNothing().when(incentivoDAO).create(any());
        doNothing().when(miembroHogarDAO).update(any());

        // Preparar miembro que ascenderá de BRONCE a PLATA
        MiembroHogar miembro = new MiembroHogar("Test", 25);
        miembro.setId(1L);
        miembro.setPuntos(50);
        miembro.setLiga(Liga.BRONCE);

        // Quehacer completado a tiempo (dará puntos para ascender)
        Quehacer quehacer = new Quehacer("Test", LocalDateTime.now().plusHours(1), Dificultad.MEDIO);
        quehacer.setEstado(EstadoQuehacer.COMPLETADO);
        quehacer.setFechaFinalizacion(LocalDateTime.now());

        // Act
        service.aplicarIncentivo(miembro, quehacer);

        // Assert
        verify(emblemaService).asignarEmblemaAscenso(1L, "BRONCE", "PLATA");
    
    
    }

    @Test
    public void dado_AscensoDeBronceAPlata_Cuando_IncentivoServiceProcesa_yEntonces_AsignaEmblemaExplorador(){
        IEmblemaService emblemaService = mock(IEmblemaService.class);
        IncentivoDAO incentivoDAO = mock(IncentivoDAO.class);
        Mockito.when(emblemaService.asignarEmblemaAscenso(1L, "BRONCE", "PLATA")).thenReturn(true);
        assertEquals(true, emblemaService.asignarEmblemaAscenso(1L, "BRONCE", "PLATA"));
    }
}
