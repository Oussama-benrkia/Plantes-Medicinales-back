package ma.m3achaba.plantes.util.images;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ControllerImages.class)
class ControllerImagesTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ImgService imgService;

    @Test
    void image_shouldReturnImage() throws Exception {
        String testFile = "test.jpg";
        byte[] imageData = "Image content".getBytes(); // Simuler le contenu
        Path testFilePath = Paths.get(System.getProperty("user.dir"), "uploads", "user", testFile);
        Files.createDirectories(testFilePath.getParent());
        Files.write(testFilePath, imageData);
        when(imgService.imageToByte("user/" + testFile)).thenReturn(imageData);
        mockMvc.perform(get("/api/image/user/{file}", testFile))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.IMAGE_JPEG))
                .andExpect(content().bytes(imageData));
        Files.deleteIfExists(testFilePath);
    }



}