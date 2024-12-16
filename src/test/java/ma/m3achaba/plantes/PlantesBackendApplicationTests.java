package ma.m3achaba.plantes;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class PlantesBackendApplicationTests {

    private MockedStatic<Files> mockedFiles;

    @BeforeEach
    void setUp() {
        mockedFiles = Mockito.mockStatic(Files.class);
    }

    @AfterEach
    void tearDown() {
        mockedFiles.close(); // Close MockedStatic to deregister
    }

    @Test
    void testDirectoryCreation() throws Exception {
        mockedFiles.when(() -> Files.exists(any(Path.class))).thenReturn(false);
        mockedFiles.when(() -> Files.createDirectories(any(Path.class))).thenReturn(null);

        CommandLineRunner commandLineRunner = args -> {
            String[] directories = {"uploads", "uploads/user", "uploads/plante", "uploads/article"};
            for (String dir : directories) {
                Path path = Paths.get(dir);
                if (!Files.exists(path)) {
                    Files.createDirectories(path);
                }
            }
        };

        commandLineRunner.run();

        for (String dir : new String[]{"uploads", "uploads/user", "uploads/plante", "uploads/article"}) {
            Path path = Paths.get(dir);
            mockedFiles.verify(() -> Files.createDirectories(path), times(1));
        }
    }

    @Test
    void testDirectoryAlreadyExists() throws Exception {
        mockedFiles.when(() -> Files.exists(any(Path.class))).thenReturn(true);

        CommandLineRunner commandLineRunner = args -> {
            String[] directories = {"uploads", "uploads/user", "uploads/plante", "uploads/article"};
            for (String dir : directories) {
                Path path = Paths.get(dir);
                if (!Files.exists(path)) {
                    Files.createDirectories(path);
                }
            }
        };

        commandLineRunner.run();

        for (String dir : new String[]{"uploads", "uploads/user", "uploads/plante", "uploads/article"}) {
            Path path = Paths.get(dir);
            mockedFiles.verify(() -> Files.createDirectories(path), never());
        }
    }

    @Test
    void testErrorWhileCreatingDirectory() throws Exception {
        mockedFiles.when(() -> Files.exists(any(Path.class))).thenReturn(false);
        mockedFiles.when(() -> Files.createDirectories(any(Path.class))).thenThrow(new IOException("Test exception"));

        CommandLineRunner commandLineRunner = args -> {
            String[] directories = {"uploads", "uploads/user", "uploads/plante", "uploads/article"};
            for (String dir : directories) {
                Path path = Paths.get(dir);
                if (!Files.exists(path)) {
                    try {
                        Files.createDirectories(path);
                    } catch (IOException e) {
                        System.err.println("Error creating directory: " + dir);
                    }
                }
            }
        };

        commandLineRunner.run();

        for (String dir : new String[]{"uploads", "uploads/user", "uploads/plante", "uploads/article"}) {
            Path path = Paths.get(dir);
            mockedFiles.verify(() -> Files.createDirectories(path), times(1));
        }
    }
}
