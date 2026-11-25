package co.ucaldas.tienda;

import co.ucaldas.tienda.domain.CalculadoraDeTotal;
import co.ucaldas.tienda.domain.Producto;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/** RN-1 y RN-2 con la fila 2: Teclado, $120.000, u = 4, d = 8 %. */
public class CalculadoraDeTotalTest {

    private static final Producto TECLADO = new Producto(1L, "Teclado", 120_000L, 30, 4, 8);

    @Test
    public void justoPorDebajoDelUmbralNoTieneDescuento() {
        assertEquals(0, CalculadoraDeTotal.descuentoPct(TECLADO, 3));
        assertEquals(360_000L, CalculadoraDeTotal.total(TECLADO, 3));
    }

    @Test
    public void elUmbralMismoYaTieneDescuento() {
        assertEquals(8, CalculadoraDeTotal.descuentoPct(TECLADO, 4));
        assertEquals(441_600L, CalculadoraDeTotal.total(TECLADO, 4));
    }

    @Test
    public void unaCantidadGrandeConservaElMismoDescuento() {
        assertEquals(2_539_200L, CalculadoraDeTotal.total(TECLADO, 23));
    }
}
