package co.ucaldas.tienda.muestra;

import co.ucaldas.tienda.Aplicacion;
import co.ucaldas.tienda.adapter.in.ConsolaPedidos;
import co.ucaldas.tienda.domain.Producto;

/** Muestra de la Hoja 1, entrando por el adaptador de entrada (la consola). */
public final class MuestraHoja1 {

    public static void main(String[] args) {
        Producto p = Aplicacion.TECLADO;
        int u = p.umbralVolumen();
        int stock = p.stock();
        ConsolaPedidos consola = Aplicacion.crearConsola();

        System.out.println(consola.ejecutar("PEDIR C-001 1 " + (u - 1)));
        System.out.println(consola.ejecutar("PEDIR C-002 1 " + u));
        System.out.println(consola.ejecutar("PEDIR C-001 1 " + (stock - 2 * u + 2)));
        System.out.println(consola.ejecutar("PEDIR C-003 1 " + (stock - 2 * u + 1)));
    }
}
