package co.ucaldas.tienda.application.port.in;

import co.ucaldas.tienda.domain.Pedido;

import java.util.Optional;

/** Puerto de entrada: lo que un cliente de la aplicación puede pedirle (RN-3, RN-4, RN-5). */
public interface GestionPedidos {

    /** @throws co.ucaldas.tienda.domain.ValidacionException si la cantidad es <= 0 o el producto no existe. */
    Pedido registrar(String clienteId, long productoId, int cantidad);

    Optional<Pedido> consultar(long pedidoId);

    /** Suma del total de los pedidos CONFIRMADO del cliente. */
    long totalComprado(String clienteId);
}
