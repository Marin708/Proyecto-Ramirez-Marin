package co.ucaldas.tienda.muestra;

import co.ucaldas.tienda.Aplicacion;
import co.ucaldas.tienda.adapter.in.ConsolaPedidos;
import co.ucaldas.tienda.domain.Producto;

/** Hoja 2 · Q3, corrida 2 (después de reiniciar, sin borrar pedidos.jsonl). */
public final class MuestraQ3Corrida2 {

    public static void main(String[] args) {
        Producto p = Aplicacion.TECLADO;
        int restante = p.stock() - (p.umbralVolumen() - 1);   // stock que dejó la corrida 1
        ConsolaPedidos consola = Aplicacion.crearConsola();
        System.out.println(consola.ejecutar("CONSULTAR 1"));
        System.out.println(consola.ejecutar("PEDIR C-002 1 " + (restante + 1)));
        System.out.println(consola.ejecutar("PEDIR C-003 1 " + restante));
    }
}
