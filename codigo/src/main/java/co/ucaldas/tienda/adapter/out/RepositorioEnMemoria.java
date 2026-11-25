package co.ucaldas.tienda.adapter.out;

import co.ucaldas.tienda.application.port.out.RepositorioTienda;
import co.ucaldas.tienda.domain.EstadoPedido;
import co.ucaldas.tienda.domain.Pedido;
import co.ucaldas.tienda.domain.Producto;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/** Adaptador de salida en memoria. Cada instancia es un arranque limpio. */
public class RepositorioEnMemoria implements RepositorioTienda {

    private final Map<Long, Producto> productos = new LinkedHashMap<>();
    private final Map<Long, Pedido> pedidos = new LinkedHashMap<>();

    public RepositorioEnMemoria(Producto... iniciales) {
        for (Producto p : iniciales) {
            productos.put(p.id(), p);
        }
    }

    @Override
    public Optional<Producto> buscarProducto(long productoId) {
        return Optional.ofNullable(productos.get(productoId));
    }

    @Override
    public long siguienteIdPedido() {
        return pedidos.size() + 1L;
    }

    @Override
    public void guardarPedido(Pedido pedido) {
        pedidos.put(pedido.id(), pedido);
        if (pedido.estado() == EstadoPedido.CONFIRMADO) {
            Producto p = productos.get(pedido.productoId());
            productos.put(p.id(), p.conStock(p.stock() - pedido.cantidad()));
        }
    }

    @Override
    public Optional<Pedido> buscarPedido(long pedidoId) {
        return Optional.ofNullable(pedidos.get(pedidoId));
    }

    @Override
    public List<Pedido> pedidosDeCliente(String clienteId) {
        List<Pedido> resultado = new ArrayList<>();
        for (Pedido p : pedidos.values()) {
            if (p.clienteId().equals(clienteId)) {
                resultado.add(p);
            }
        }
        return resultado;
    }
}
