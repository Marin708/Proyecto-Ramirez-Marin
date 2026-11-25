package co.ucaldas.tienda.adapter.out;

import co.ucaldas.tienda.application.port.out.RepositorioTienda;
import co.ucaldas.tienda.domain.EstadoPedido;
import co.ucaldas.tienda.domain.Pedido;
import co.ucaldas.tienda.domain.Producto;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Adaptador de salida en archivo: un objeto JSON por línea en pedidos.jsonl.
 * Cada línea guarda el pedido y el stock que consumió ("stockConsumido": la
 * cantidad si está CONFIRMADO, 0 si está RECHAZADO). Al abrirlo se relee el
 * archivo: el stock vigente es el inicial menos lo consumido y el siguiente id
 * continúa la secuencia.
 */
public class RepositorioArchivoJsonl implements RepositorioTienda {

    private static final Pattern LINEA = Pattern.compile(
            "\\{\"id\":(\\d+),\"clienteId\":\"((?:[^\"\\\\]|\\\\.)*)\",\"productoId\":(\\d+),"
                    + "\"cantidad\":(\\d+),\"estado\":\"(\\w+)\",\"total\":(\\d+),\"stockConsumido\":(\\d+)\\}");

    private final Path archivo;
    private final Map<Long, Producto> productos = new LinkedHashMap<>();
    private final Map<Long, Pedido> pedidos = new LinkedHashMap<>();

    public RepositorioArchivoJsonl(Path archivo, Producto... iniciales) {
        this.archivo = archivo;
        for (Producto p : iniciales) {
            productos.put(p.id(), p);
        }
        if (Files.exists(archivo)) {
            cargar();
        }
    }

    private void cargar() {
        try {
            for (String linea : Files.readAllLines(archivo, StandardCharsets.UTF_8)) {
                if (linea.isBlank()) {
                    continue;
                }
                Matcher m = LINEA.matcher(linea);
                if (!m.matches()) {
                    throw new IllegalStateException("línea inválida en " + archivo + ": " + linea);
                }
                Pedido pedido = new Pedido(Long.parseLong(m.group(1)), desescapar(m.group(2)),
                        Long.parseLong(m.group(3)), Integer.parseInt(m.group(4)),
                        EstadoPedido.valueOf(m.group(5)), Long.parseLong(m.group(6)));
                pedidos.put(pedido.id(), pedido);
                Producto p = productos.get(pedido.productoId());
                if (p != null) {
                    productos.put(p.id(), p.conStock(p.stock() - Integer.parseInt(m.group(7))));
                }
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public Optional<Producto> buscarProducto(long productoId) {
        return Optional.ofNullable(productos.get(productoId));
    }

    @Override
    public long siguienteIdPedido() {
        return pedidos.keySet().stream().mapToLong(Long::longValue).max().orElse(0L) + 1L;
    }

    @Override
    public void guardarPedido(Pedido pedido) {
        int consumido = pedido.estado() == EstadoPedido.CONFIRMADO ? pedido.cantidad() : 0;
        String linea = "{\"id\":" + pedido.id()
                + ",\"clienteId\":\"" + escapar(pedido.clienteId()) + "\""
                + ",\"productoId\":" + pedido.productoId()
                + ",\"cantidad\":" + pedido.cantidad()
                + ",\"estado\":\"" + pedido.estado() + "\""
                + ",\"total\":" + pedido.total()
                + ",\"stockConsumido\":" + consumido + "}\n";
        try {
            Files.writeString(archivo, linea, StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        pedidos.put(pedido.id(), pedido);
        Producto p = productos.get(pedido.productoId());
        if (consumido > 0) {
            productos.put(p.id(), p.conStock(p.stock() - consumido));
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

    private static String escapar(String s) {
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    private static String desescapar(String s) {
        return s.replace("\\\"", "\"").replace("\\\\", "\\");
    }
}
