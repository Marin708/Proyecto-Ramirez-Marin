# ADR-0001 · Elegir arquitectura hexagonal

Variante: Q3 / fila 2

- Fecha: 2026-11-24 21:03
- Estado: aceptada

## Contexto
La tienda vende un solo producto, el Teclado (id 1, $120.000, stock 30, umbral de volumen u = 4, descuento d = 8 %), y registra pedidos de una línea que descuentan ese stock. El atributo prioritario es Q3, sustituibilidad de la infraestructura: todavía no está decidido dónde van a vivir los datos y la regla de negocio (RN-1 a RN-3) no debe moverse cuando se decida. La Hoja 2 de Q3 ya es pública: los pedidos y el stock consumido pasarán a un archivo pedidos.jsonl que sobrevive al reinicio, y las pruebas de la Hoja 1 tienen que pasar sin editarse. Hoy el almacenamiento será en memoria y la persistencia en archivo es el cambio que se espera.

## Alternativas consideradas
- Capas — descartada porque, para Q3, en capas el servicio depende directamente del repositorio concreto: cambiar de memoria a archivo obliga a abrir el servicio que registra el pedido (y que calcula el total del Teclado) o sus pruebas, y la Hoja 2 de Q3 prohíbe editar las pruebas de la Hoja 1 para que el cambio pase. La guía de capas solo exige que el controlador no salte al repositorio; no protege a la regla de negocio de la tecnología.
- Eventos — descartada porque con un único producto, stock de 30 unidades y un umbral de 4 el pedido se resuelve en una sola decisión síncrona; un bus de eventos añade consistencia eventual y la muestra de la Hoja 1 (la línea 4 solo cabe si la 3 ya liberó o no consumió stock) tendría que esperar al procesamiento asíncrono. Q3 pide cambiar el almacenamiento, no desacoplar reacciones (eso es Q2).
- Monolito modular — descartada porque sus fronteras sirven cuando hay varios módulos (catalogo, pedidos) que se hablan por API pública; aquí hay un solo producto y la fila 2 no justifica dos módulos. Con ApplicationModules.verify() se verifican fronteras entre módulos, pero no que el dominio ignore el almacenamiento, que es lo que Q3 necesita.

## Decisión
Se elige arquitectura hexagonal en Java plano: dominio (Producto, Pedido, CalculadoraDeTotal), aplicación (ServicioPedidos con un puerto de entrada y el puerto de salida RepositorioTienda) y adaptadores (consola de entrada; repositorio en memoria de salida). Persistir en archivo será un segundo adaptador de salida del mismo puerto.

## Consecuencias
**Se gana:** el cambio de Q3 queda localizado en un adaptador nuevo y en el punto de composición; el dominio y la aplicación no deberían enterarse de que los datos cambiaron de lugar, y las pruebas de la Hoja 1 se escriben contra el puerto y no contra la tecnología.
**Se pierde:** más piezas para un producto y una regla: un puerto de entrada, un puerto de salida y una clase de adaptación por tecnología, es decir, código de traducción entre el Pedido del dominio y la línea del archivo que hay que mantener. Si el próximo cambio fuera solo agregar un campo a Producto, capas bastaría y costaría menos.
**Predicción del costo de la Hoja 2:** se esperan 4 archivos creados: codigo/src/main/java/co/ucaldas/tienda/adapter/out/RepositorioArchivoJsonl.java, codigo/src/main/java/co/ucaldas/tienda/muestra/MuestraQ3Corrida1.java, codigo/src/main/java/co/ucaldas/tienda/muestra/MuestraQ3Corrida2.java y codigo/src/test/java/co/ucaldas/tienda/RepositorioArchivoJsonlTest.java; y 2 archivos modificados: codigo/src/main/java/co/ucaldas/tienda/Aplicacion.java (el punto de composición, que cambia el adaptador) y LEEME.md (el segundo comando). Se espera NO tocar la clase que calcula el total (CalculadoraDeTotal), NO tocar la que registra el pedido (ServicioPedidos), NO tocar el puerto RepositorioTienda ni ningún archivo de pruebas de la Hoja 1. Si el diff muestra una modificación en alguna de esas, la predicción falló.
