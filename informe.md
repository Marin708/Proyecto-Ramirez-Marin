# Informe · Tienda hexagonal, variante Q3 / fila 2 (Teclado)

Juan Camilo Ramirez y Gustavo Adolfo Marin Muñoz. La muestra de la Hoja 1 funcionaba por completo al colocar la etiqueta hoja-1.

| Medida | Resultado |
|---|---|
| Archivos creados | 4 (`RepositorioArchivoJsonl.java`, `MuestraQ3Corrida1.java`, `MuestraQ3Corrida2.java`, `RepositorioArchivoJsonlTest.java`) |
| Archivos modificados | 2 (`Aplicacion.java`, `LEEME.md`) |
| ¿Se modificó el archivo o la clase que calcula el total? | No (`CalculadoraDeTotal` sin cambios) |
| ¿Se modificó el archivo o la clase que registra el pedido? | No (`ServicioPedidos` sin cambios) |
| ¿Se tocó una prueba de la Hoja 1? | No (solo se agregó una prueba nueva, la del adaptador) |

Fuente: `git diff --name-status hoja-1 final` en `evidencia.txt`.

**Primera: predicción contra medición.** El ADR-0001 predijo 4 archivos creados y 2 modificados, y no tocar CalculadoraDeTotal, ServicioPedidos, el puerto RepositorioTienda ni las pruebas de la Hoja 1. La tabla midió exactamente eso: 4 creados (RepositorioArchivoJsonl, las dos muestras y su prueba), 2 modificados (Aplicacion y LEEME.md) y «No» en las tres preguntas finales, de modo que la predicción acertó en todo. Hay que decirlo con cuidado: la Hoja 2 era pública y el diseño es pequeño, así que acertar era fácil. Lo que la predicción no vio es un costo que el diff no muestra: la regla del id consecutivo quedó implementada una vez por adaptador, y solo un comentario del puerto las mantiene de acuerdo.

**Segunda: el límite.** Si la cooperativa pide que cada pedido CONFIRMADO además facture y avise al cliente, y que luego se sumen más reacciones, la hexagonal elegida sería peor que eventos. Aquí el lugar natural es ServicioPedidos, la clase que registra el pedido: cada reacción nueva obliga a reabrirla, con el riesgo de que una factura fallida afecte el registro. La guía hexagonal advierte que está «mejor preparada para los cambios que todavía no tienes» y que sus mapeos y archivos extra solo valen si el entorno del negocio cambia, y su tabla de cuándo conviene habla de reglas y de bases de datos cambiantes, no de reacciones en cadena. En eventos, sumar un consumidor no toca al productor, que es justo lo que ese cambio necesita.

**Tercera: lo que la regla no ve.** La violación inducida fue un import de RepositorioEnMemoria, del paquete adapter, dentro de domain/Producto.java. ReglaArquitecturaTest falló y el grep documentado la imprimió, así que la regla sí la detectó antes de revertirla con git checkout. En cambio no detectaría que domain usara java.nio.file.Files para leer un archivo, porque la regla solo prohíbe Spring, jakarta, application y adapter, y java.nio no está en esa lista. Tampoco detectaría una referencia con nombre completo sin import, porque la prueba solo lee líneas que empiezan con import.
