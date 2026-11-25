package co.ucaldas.tienda;

import co.ucaldas.tienda.adapter.in.ConsolaPedidos;
import co.ucaldas.tienda.adapter.out.RepositorioArchivoJsonl;
import co.ucaldas.tienda.application.ServicioPedidos;
import co.ucaldas.tienda.application.port.in.GestionPedidos;
import co.ucaldas.tienda.application.port.out.RepositorioTienda;
import co.ucaldas.tienda.domain.Producto;

import java.nio.file.Paths;

/**
 * Punto de composición: el único lugar que conoce los adaptadores concretos.
 * Fila 2 de la tabla: Teclado, $120.000, stock 30, u = 4, d = 8 %.
 */
public final class Aplicacion {

    public static final Producto TECLADO = new Producto(1L, "Teclado", 120_000L, 30, 4, 8);

    private Aplicacion() {
    }

    public static ConsolaPedidos crearConsola() {
        RepositorioTienda repositorio = new RepositorioArchivoJsonl(Paths.get("pedidos.jsonl"), TECLADO);
        GestionPedidos pedidos = new ServicioPedidos(repositorio);
        return new ConsolaPedidos(pedidos);
    }
}
