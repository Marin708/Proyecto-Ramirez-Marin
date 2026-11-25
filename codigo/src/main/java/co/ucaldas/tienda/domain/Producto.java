package co.ucaldas.tienda.domain;

/**
 * Producto de la tienda. El umbral de volumen y el descuento hacen parte del
 * producto porque la tabla de la fila los fija por producto (RN-1).
 * Todo en pesos enteros y unidades enteras: no hay decimales que redondear.
 */
public record Producto(long id, String nombre, long precio, int stock,
                       int umbralVolumen, int descuentoPct) {

    public Producto conStock(int nuevoStock) {
        return new Producto(id, nombre, precio, nuevoStock, umbralVolumen, descuentoPct);
    }
}
