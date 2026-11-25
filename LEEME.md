# Tienda hexagonal · Q3 / fila 2 (Teclado)

Proyecto en pareja de Arquitectura de Software Empresarial (248G8F). Java 21 plano, sin Spring ni HTTP.
Fila 2: Teclado, $120.000, stock 30, umbral u = 4, descuento d = 8 %. Variante Q3 (sustituibilidad).

Estructura de `codigo/src/main/java/co/ucaldas/tienda/`:
`domain` (Producto, Pedido, CalculadoraDeTotal), `application` (ServicioPedidos y los puertos `port.in` / `port.out`),
`adapter.in` (ConsolaPedidos), `adapter.out` (almacenamiento), `muestra` (programas de muestra) y `Aplicacion` (punto de composición).

## Muestra de la Hoja 1 (arranque limpio)

El almacenamiento es el archivo `pedidos.jsonl` del directorio desde el que se corre; arranque limpio es borrarlo antes.

    cd codigo && mvn -q -DskipTests compile && rm -f pedidos.jsonl
    java -cp target/classes co.ucaldas.tienda.muestra.MuestraHoja1

## Muestra de la Hoja 2 (Q3, datos en archivo), dos corridas en el mismo directorio

El archivo se borra antes de la corrida 1 y no entre las dos corridas. Se corre desde `codigo/` después de compilar.

    rm -f pedidos.jsonl && java -cp target/classes co.ucaldas.tienda.muestra.MuestraQ3Corrida1
    java -cp target/classes co.ucaldas.tienda.muestra.MuestraQ3Corrida2

## Pruebas y regla de arquitectura

    cd codigo && mvn -q test

La regla (`ReglaArquitecturaTest`): nada bajo `domain` importa Spring, jakarta, `application` ni `adapter`, y nada bajo
`application` importa un `adapter`. Equivalente con grep, desde la raíz del proyecto (no debe imprimir nada):

    grep -rnE "^import (org\.springframework|jakarta|co\.ucaldas\.tienda\.(application|adapter))" codigo/src/main/java/co/ucaldas/tienda/domain
    grep -rnE "^import co\.ucaldas\.tienda\.adapter" codigo/src/main/java/co/ucaldas/tienda/application
