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

    @Test
    void deleteImage_shouldReturnFalseIfIOExceptionOccurs() {
        String testFile = "non_existent.jpg";

        // Simulate an IOException
        mockStatic(Files.class, invocation -> {
            if ("exists".equals(invocation.getMethod().getName())) {
                return true; // Simulate file exists
            }
            if ("delete".equals(invocation.getMethod().getName())) {
                throw new IOException("Test exception");
            }
            return invocation.callRealMethod();
        });

        Boolean result = imgService.deleteImage(testFile);

        assertFalse(result);
    }

    @Test
    void addImage_shouldReturnEmptyStringIfFileIsNull() {
        String result = imgService.addImage(null, ImagesFolder.PLANTE);
        assertEquals("", result);
    }

    @Test
    void addImage_shouldReturnEmptyStringIfFileIsEmpty() {
        when(mockFile.isEmpty()).thenReturn(true);

        String result = imgService.addImage(mockFile, ImagesFolder.PLANTE);

        assertEquals("", result);
    }

    @Test
    void addImage_shouldReturnEmptyStringIfFileNameIsNull() {
        when(mockFile.getOriginalFilename()).thenReturn(null);

        String result = imgService.addImage(mockFile, ImagesFolder.PLANTE);

        assertEquals("", result);
    }

    @Test
    void addImage_shouldReturnEmptyStringIfIOExceptionOccurs() throws IOException {
        String fileName = "test.jpg";
        when(mockFile.getOriginalFilename()).thenReturn(fileName);
        when(mockFile.isEmpty()).thenReturn(false);
        doThrow(new IOException("Test exception")).when(mockFile).transferTo(any(File.class));

        String result = imgService.addImage(mockFile, ImagesFolder.PLANTE);

        assertEquals("", result);
    }
}
