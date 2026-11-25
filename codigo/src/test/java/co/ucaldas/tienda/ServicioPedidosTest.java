package co.ucaldas.tienda;

import co.ucaldas.tienda.adapter.out.RepositorioEnMemoria;
import co.ucaldas.tienda.application.ServicioPedidos;
import co.ucaldas.tienda.domain.EstadoPedido;
import co.ucaldas.tienda.domain.Pedido;
import co.ucaldas.tienda.domain.Producto;
import co.ucaldas.tienda.domain.ValidacionException;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/** RN-3, RN-4 y RN-5 contra el puerto de entrada, con el adaptador en memoria. */
public class ServicioPedidosTest {

    private ServicioPedidos servicio;

    @Before
    public void preparar() {
        servicio = new ServicioPedidos(
                new RepositorioEnMemoria(new Producto(1L, "Teclado", 120_000L, 30, 4, 8)));
    }

    @Test
    public void confirmaYAplicaDescuentoDesdeElUmbral() {
        Pedido p = servicio.registrar("C-001", 1L, 4);
        assertEquals(1L, p.id());
        assertEquals(EstadoPedido.CONFIRMADO, p.estado());
        assertEquals(441_600L, p.total());
    }

    @Test
    public void rechazaSiSePasaDelStockSinConsumirloYElSiguienteCabe() {
        servicio.registrar("C-001", 1L, 3);   // stock 27
        servicio.registrar("C-002", 1L, 4);   // stock 23
        Pedido rechazado = servicio.registrar("C-001", 1L, 24);
        assertEquals(EstadoPedido.RECHAZADO, rechazado.estado());
        assertEquals(0L, rechazado.total());
        assertEquals(3L, rechazado.id());     // el rechazado también recibe id
        Pedido justo = servicio.registrar("C-003", 1L, 23);
        assertEquals(EstadoPedido.CONFIRMADO, justo.estado());   // deja el stock en 0
        assertEquals(2_539_200L, justo.total());
        assertEquals(EstadoPedido.RECHAZADO, servicio.registrar("C-003", 1L, 1).estado());
    }

    @Test(expected = ValidacionException.class)
    public void cantidadCeroEsErrorDeValidacion() {
        servicio.registrar("C-001", 1L, 0);
    }

    @Test(expected = ValidacionException.class)
    public void productoInexistenteEsErrorDeValidacion() {
        servicio.registrar("C-001", 99L, 1);
    }

    @Test
    public void unErrorDeValidacionNoConsumeId() {
        try {
            servicio.registrar("C-001", 1L, -1);
        } catch (ValidacionException esperado) {
            // no debe haber recibido id
        }
        assertEquals(1L, servicio.registrar("C-001", 1L, 1).id());
    }

    @Test
    public void consultaPorIdYTotalPorClienteSoloSumaConfirmados() {
        servicio.registrar("C-001", 1L, 3);    // 360000
        servicio.registrar("C-001", 1L, 4);    // 441600
        servicio.registrar("C-001", 1L, 30);   // rechazado
        assertTrue(servicio.consultar(2L).isPresent());
        assertFalse(servicio.consultar(9L).isPresent());
        assertEquals(801_600L, servicio.totalComprado("C-001"));
    }
}
