package co.ucaldas.tienda;

import co.ucaldas.tienda.adapter.out.RepositorioArchivoJsonl;
import co.ucaldas.tienda.application.ServicioPedidos;
import co.ucaldas.tienda.domain.EstadoPedido;
import co.ucaldas.tienda.domain.Pedido;
import co.ucaldas.tienda.domain.Producto;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/** El adaptador de archivo: lo guardado sobrevive a "reiniciar" (abrir otra instancia sobre el mismo archivo). */
public class RepositorioArchivoJsonlTest {

    private static final Producto TECLADO = new Producto(1L, "Teclado", 120_000L, 30, 4, 8);

    @Rule
    public TemporaryFolder carpeta = new TemporaryFolder();

    @Test
    public void elStockConsumidoLosPedidosYElIdSiguienteSobrevivenAlReinicio() throws IOException {
        Path archivo = carpeta.getRoot().toPath().resolve("pedidos.jsonl");

        ServicioPedidos antes = new ServicioPedidos(new RepositorioArchivoJsonl(archivo, TECLADO));
        antes.registrar("C-001", 1L, 3);

        ServicioPedidos despues = new ServicioPedidos(new RepositorioArchivoJsonl(archivo, TECLADO));
        assertEquals(360_000L, despues.consultar(1L).orElseThrow().total());
        Pedido rechazado = despues.registrar("C-002", 1L, 28);    // quedan 27
        assertEquals(2L, rechazado.id());
        assertEquals(EstadoPedido.RECHAZADO, rechazado.estado());
        Pedido justo = despues.registrar("C-003", 1L, 27);
        assertEquals(3L, justo.id());
        assertEquals(EstadoPedido.CONFIRMADO, justo.estado());
        assertEquals(3, Files.readAllLines(archivo).size());      // el rechazado también se guarda
    }

    @Test
    public void sinArchivoElArranqueEsLimpio() {
        Path archivo = carpeta.getRoot().toPath().resolve("no-existe.jsonl");
        RepositorioArchivoJsonl repo = new RepositorioArchivoJsonl(archivo, TECLADO);
        assertEquals(1L, repo.siguienteIdPedido());
        assertEquals(30, repo.buscarProducto(1L).orElseThrow().stock());
        assertTrue(repo.pedidosDeCliente("C-001").isEmpty());
    }
}
