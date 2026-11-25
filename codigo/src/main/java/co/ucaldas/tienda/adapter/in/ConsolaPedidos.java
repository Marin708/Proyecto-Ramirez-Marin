package co.ucaldas.tienda.adapter.in;

import co.ucaldas.tienda.application.port.in.GestionPedidos;
import co.ucaldas.tienda.domain.Pedido;
import co.ucaldas.tienda.domain.ValidacionException;

/**
 * Adaptador de entrada: traduce un comando de texto a una llamada al puerto
 * GestionPedidos y la respuesta a la línea de la muestra.
 *
 * Comandos: PEDIR cliente productoId cantidad | CONSULTAR id | TOTAL cliente
 */
public class ConsolaPedidos {

    private final GestionPedidos pedidos;

    public ConsolaPedidos(GestionPedidos pedidos) {
        this.pedidos = pedidos;
    }

    public String ejecutar(String comando) {
        String[] t = comando.trim().split("\\s+");
        try {
            switch (t[0]) {
                case "PEDIR":
                    return linea(pedidos.registrar(t[1], Long.parseLong(t[2]), Integer.parseInt(t[3])));
                case "CONSULTAR":
                    return pedidos.consultar(Long.parseLong(t[1])).map(ConsolaPedidos::linea)
                            .orElse("ERROR pedido inexistente");
                case "TOTAL":
                    return "TOTAL " + t[1] + " total=" + pedidos.totalComprado(t[1]);
                default:
                    return "ERROR comando desconocido: " + t[0];
            }
        } catch (ValidacionException e) {
            return "ERROR " + e.getMessage();
        } catch (RuntimeException e) {
            return "ERROR comando mal formado: " + comando;
        }
    }

    static String linea(Pedido p) {
        return "PEDIDO " + p.id() + " " + p.estado() + " total=" + p.total();
    }
}
