package service;

import model.MiembroHogar;
import org.junit.Test;
import static org.junit.Assert.*;

public class LigaServiceTest {

    @Test
    public void dadoUsuarioEnPlata_cuandoPierdePuntos_entoncesDesciendeABronceSinBonificacion() {
        //Crear usuario en Plata (500-1499 puntos)
        MiembroHogar usuario = new MiembroHogar("Carlos", 28);
        usuario.setPuntos(510);

        LigaService ligaService = new LigaService();
        ligaService.actualizarLiga(usuario); // Establecer liga inicial

        String ligaAnterior = usuario.getLiga();
        assertEquals("PLATA", ligaAnterior);

        //Perder puntos suficientes para descender a Bronce
        ligaService.actualizarPuntosYLiga(usuario, -20); // 510 - 20 = 490

        //Verificar descenso exacto sin bonificaciones
        assertEquals("BRONCE", usuario.getLiga());
        assertEquals(490, usuario.getPuntos()); // Sin penalizaciones extras
    }
}