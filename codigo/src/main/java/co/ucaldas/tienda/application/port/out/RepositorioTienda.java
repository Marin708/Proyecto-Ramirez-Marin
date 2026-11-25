package co.ucaldas.tienda.application.port.out;

import co.ucaldas.tienda.domain.Pedido;
import co.ucaldas.tienda.domain.Producto;

import java.util.List;
import java.util.Optional;

/**
 * Puerto de salida: lo que la aplicación necesita de los datos, dicho en el
 * idioma del dominio. No habla de tablas, archivos ni SQL.
 */
public interface RepositorioTienda {

    /** El producto con su stock vigente (ya descontados los pedidos CONFIRMADO). */
    Optional<Producto> buscarProducto(long productoId);

    /** El id que recibirá el próximo pedido que pase la validación. Consecutivo desde 1. */
    long siguienteIdPedido();

    /**
     * Guarda el pedido. Si está CONFIRMADO, en la misma operación descuenta
     * su cantidad del stock del producto; si está RECHAZADO, no consume stock.
     */
    void guardarPedido(Pedido pedido);

    Optional<Pedido> buscarPedido(long pedidoId);

    List<Pedido> pedidosDeCliente(String clienteId);
}
