package ma.m3achaba.plantes.util.images;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ImgServiceTest {

    private ImgService imgService;

    @Mock
    private MultipartFile mockFile;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        imgService = new ImgService();
    }

    @Test
    void imageToByte_shouldReturnFileContent() throws IOException {
        String testFile = "test.jpg";
        Path testPath = Paths.get(System.getProperty("user.dir"), "uploads", testFile);
        Files.createDirectories(testPath.getParent());
        Files.writeString(testPath, "Test content");
        byte[] result = imgService.imageToByte(testFile);
        assertNotNull(result);
        assertEquals("Test content", new String(result));
        Files.deleteIfExists(testPath);
    }



    @Test
    void deleteImage_shouldDeleteFile() throws IOException {
        String testFile = "test_delete.jpg";
        Path testPath = Paths.get(System.getProperty("user.dir"), "uploads", testFile);
        Files.createDirectories(testPath.getParent());
        Files.writeString(testPath, "Test content");
        Boolean isDeleted = imgService.deleteImage(testFile);
        assertTrue(isDeleted);
        assertFalse(Files.exists(testPath));
    }
}
