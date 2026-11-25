package co.ucaldas.tienda.domain;

/** Pedido de una línea. Un RECHAZADO tiene total 0 y también recibe id. */
public record Pedido(long id, String clienteId, long productoId, int cantidad,
                     EstadoPedido estado, long total) {
}
