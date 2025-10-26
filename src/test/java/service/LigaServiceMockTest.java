package service;

import model.Liga;
import model.MiembroHogar;
import org.junit.Test;
import repository.AchievementRepository;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class LigaServiceMockTest {

    @Test
    public void dadoUsuarioConLogroExistente_cuandoAsciendeNuevamente_entoncesNoSeOtorgaBonificacionDuplicada() {
        //Crear mock del repositorio
        AchievementRepository mockRepo = mock(AchievementRepository.class);
        //Simular que el usuario ya tiene el logro
        Long usuarioId = 1L;
        when(mockRepo.tieneLogro(usuarioId, "AscensoAPLATA")).thenReturn(true);
        //Crear usuario en Bronce a punto de ascender
        MiembroHogar usuario = new MiembroHogar("Juan", 25);
        usuario.setId(usuarioId);
        usuario.setPuntos(490); // Bronce < 500
        // Crear servicio inyectando el mock
        LigaService ligaService = new LigaService(mockRepo);
        ligaService.actualizarLiga(usuario);
        assertEquals(Liga.BRONCE, usuario.getLiga());
        //Ganar puntos para ascender a Plata
        ligaService.actualizarPuntosYLiga(usuario, 20); // 490 + 20 = 510
        //Verificar que ascendió a Plata
        assertEquals(Liga.PLATA, usuario.getLiga());
        //Verificar que NO se llamó a guardarLogro
        verify(mockRepo, never()).guardarLogro(anyLong(), eq("AscensoAPLATA"));
        //Verificar que solo tiene 510 puntos (sin bonificación de +50)
        assertEquals(510, usuario.getPuntos());
    }
}