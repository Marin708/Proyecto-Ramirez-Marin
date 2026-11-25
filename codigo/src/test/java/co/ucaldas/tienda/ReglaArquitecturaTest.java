package co.ucaldas.tienda;

import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.Assert.assertTrue;

/**
 * Regla de dependencias de la hexagonal, verificada leyendo los import:
 *  - nada bajo domain importa Spring, jakarta, application ni adapter;
 *  - nada bajo application importa un adapter.
 * (Se corre desde la carpeta codigo/, que es donde Maven ejecuta las pruebas.)
 */
public class ReglaArquitecturaTest {

    private static final String BASE = "src/main/java/co/ucaldas/tienda/";

    @Test
    public void elDominioNoDependeDeNadaDeAfuera() throws IOException {
        List<String> violaciones = revisar("domain",
                "org.springframework", "jakarta", "co.ucaldas.tienda.application", "co.ucaldas.tienda.adapter");
        assertTrue("domain importa lo que no debe: " + violaciones, violaciones.isEmpty());
    }

    @Test
    public void laAplicacionNoImportaAdaptadores() throws IOException {
        List<String> violaciones = revisar("application", "co.ucaldas.tienda.adapter");
        assertTrue("application importa un adapter: " + violaciones, violaciones.isEmpty());
    }

    private static List<String> revisar(String paquete, String... prohibidos) throws IOException {
        List<String> violaciones = new ArrayList<>();
        try (Stream<Path> archivos = Files.walk(Paths.get(BASE + paquete))) {
            for (Path archivo : (Iterable<Path>) archivos.filter(a -> a.toString().endsWith(".java"))::iterator) {
                for (String linea : Files.readAllLines(archivo)) {
                    String l = linea.trim();
                    if (!l.startsWith("import ")) {
                        continue;
                    }
                    for (String prohibido : prohibidos) {
                        if (l.substring(7).startsWith(prohibido)) {
                            violaciones.add(archivo.getFileName() + " -> " + l);
                        }
                    }
                }
            }
        }
        return violaciones;
    }
}
