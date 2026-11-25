# ADR-0002 · Los datos van a un archivo: un adaptador nuevo, no un cambio en el servicio

Variante: Q3 / fila 2

- Fecha: 2026-11-24 21:06
- Estado: aceptada (ADR-0001 se mantiene)

## Contexto
La Hoja 2 de Q3 pide guardar pedidos y stock consumido en pedidos.jsonl, un objeto JSON por línea, de modo que al reiniciar se conserven los pedidos (incluidos los RECHAZADO), el stock del Teclado no vuelva a 30 y el id siguiente continúe la secuencia. Además las pruebas de la Hoja 1 no podían editarse. La etiqueta hoja-1 fija el punto de partida y la etiqueta final cierra el código de esta decisión.

## Alternativas consideradas
- Modificar RepositorioEnMemoria para que escriba el archivo — descartada porque las pruebas de la Hoja 1 (ServicioPedidosTest) lo usan como doble en memoria; convertirlo en archivo las acoplaría al disco y a la carpeta de trabajo, justo lo que Q3 pide evitar, y se perdería el adaptador barato para las pruebas del Teclado.
- Escribir el archivo desde ServicioPedidos — descartada porque esa clase registra el pedido y aplica RN-3; mezclar allí entrada/salida de archivos obligaría a tocar el código del stock de 30 unidades y a editar las pruebas de la Hoja 1, que la Hoja 2 prohíbe.
- Un adaptador de salida nuevo, RepositorioArchivoJsonl, que implementa el mismo puerto RepositorioTienda, más el cambio de una línea de composición en Aplicacion — elegida.

## Decisión
Se agregó adapter/out/RepositorioArchivoJsonl.java, que relee pedidos.jsonl al abrirse y calcula el stock vigente como el inicial menos el campo stockConsumido de cada línea. Aplicacion.crearConsola() pasó a construir ese adaptador; el dominio, el servicio y el puerto no se tocaron. Se agregaron dos muestras de corrida (MuestraQ3Corrida1 y MuestraQ3Corrida2) y una prueba del adaptador, RepositorioArchivoJsonlTest, nueva y no una edición de las de la Hoja 1.

## Consecuencias
**Se gana:** el cambio de almacenamiento costó 4 archivos creados y 2 modificados (git diff --name-status hoja-1 final), sin modificar CalculadoraDeTotal, ServicioPedidos, RepositorioTienda ni ninguna prueba de la Hoja 1, que siguieron pasando sin editarse. Las muestras de la Hoja 1 y de la Hoja 2 dan las líneas esperadas de la fila 2.
**Se pierde:** lo que el ADR-0001 dijo que se perdería ocurrió: RepositorioArchivoJsonl es código de traducción mantenido a mano entre Pedido y la línea JSON (un patrón de lectura y un escape de texto escritos a mano), y si se agrega un campo a Pedido el compilador no avisa que falta en la línea. Apareció además un costo que el ADR-0001 no nombró: la regla del id consecutivo y el efecto de guardarPedido sobre el stock están implementados dos veces, una por adaptador, y los protege solo el comentario del puerto, no una prueba común a ambos.
**Veredicto sobre la predicción del ADR-0001:** acertó: se esperaban 4 archivos creados y 2 modificados con exactamente esos nombres, y no tocar el cálculo del total, el registro del pedido ni las pruebas de la Hoja 1; el diff coincide. Como la Hoja 2 era pública y el diseño es pequeño, el acierto es menos sorprendente de lo que sería con un cambio imprevisto. Hoy se decidiría igual.
