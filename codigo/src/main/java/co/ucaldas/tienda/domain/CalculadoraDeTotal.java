package co.ucaldas.tienda.domain;

/**
 * RN-1 y RN-2: la única clase que sabe cómo se calcula el total.
 * total = cantidad x precio x (100 - descuento) / 100, en pesos enteros.
 */
public final class CalculadoraDeTotal {

    private CalculadoraDeTotal() {
    }

    /** RN-1: d % si cantidad >= u; 0 % si no. */
    public static int descuentoPct(Producto producto, int cantidad) {
        return cantidad >= producto.umbralVolumen() ? producto.descuentoPct() : 0;
    }

    /** RN-2. Con los números de la tabla la división es siempre exacta. */
    public static long total(Producto producto, int cantidad) {
        long bruto = (long) cantidad * producto.precio();
        return bruto * (100 - descuentoPct(producto, cantidad)) / 100;
    }
}
