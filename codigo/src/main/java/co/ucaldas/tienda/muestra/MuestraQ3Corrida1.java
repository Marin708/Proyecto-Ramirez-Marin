package co.ucaldas.tienda.muestra;

import co.ucaldas.tienda.Aplicacion;
import co.ucaldas.tienda.adapter.in.ConsolaPedidos;
import co.ucaldas.tienda.domain.Producto;

/** Hoja 2 · Q3, corrida 1 (arranque limpio): un pedido de u - 1 unidades, y la aplicación se detiene. */
public final class MuestraQ3Corrida1 {

    public static void main(String[] args) {
        Producto p = Aplicacion.TECLADO;
        ConsolaPedidos consola = Aplicacion.crearConsola();
        System.out.println(consola.ejecutar("PEDIR C-001 1 " + (p.umbralVolumen() - 1)));
    }
}
