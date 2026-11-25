package co.ucaldas.tienda.application;

import co.ucaldas.tienda.application.port.in.GestionPedidos;
import co.ucaldas.tienda.application.port.out.RepositorioTienda;
import co.ucaldas.tienda.domain.CalculadoraDeTotal;
import co.ucaldas.tienda.domain.EstadoPedido;
import co.ucaldas.tienda.domain.Pedido;
import co.ucaldas.tienda.domain.Producto;
import co.ucaldas.tienda.domain.ValidacionException;

import java.util.Optional;

/** Caso de uso: registra el pedido. Recibe el puerto de salida por constructor. */
public class ServicioPedidos implements GestionPedidos {

    private final RepositorioTienda repositorio;

    public ServicioPedidos(RepositorioTienda repositorio) {
        this.repositorio = repositorio;
    }

    @Override
    public Pedido registrar(String clienteId, long productoId, int cantidad) {
        if (cantidad <= 0) {
            throw new ValidacionException("la cantidad debe ser mayor que cero: " + cantidad);
        }
        Producto producto = repositorio.buscarProducto(productoId)
                .orElseThrow(() -> new ValidacionException("producto inexistente: " + productoId));

        long id = repositorio.siguienteIdPedido();
        Pedido pedido;
        if (cantidad > producto.stock()) {
            pedido = new Pedido(id, clienteId, productoId, cantidad, EstadoPedido.RECHAZADO, 0L);
        } else {
            long total = CalculadoraDeTotal.total(producto, cantidad);
            pedido = new Pedido(id, clienteId, productoId, cantidad, EstadoPedido.CONFIRMADO, total);
        }
        repositorio.guardarPedido(pedido);
        return pedido;
    }

    @Override
    public Optional<Pedido> consultar(long pedidoId) {
        return repositorio.buscarPedido(pedidoId);
    }

    @Override
    public long totalComprado(String clienteId) {
        return repositorio.pedidosDeCliente(clienteId).stream()
                .filter(p -> p.estado() == EstadoPedido.CONFIRMADO)
                .mapToLong(Pedido::total)
                .sum();
    }
}
