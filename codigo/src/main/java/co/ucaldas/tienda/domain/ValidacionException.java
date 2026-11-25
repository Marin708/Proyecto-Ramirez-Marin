package co.ucaldas.tienda.domain;

/** RN-5: cantidad <= 0 o producto inexistente. No es un pedido RECHAZADO y no consume id. */
public class ValidacionException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public ValidacionException(String mensaje) {
        super(mensaje);
    }
}
