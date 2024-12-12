package ma.m3achaba.plantes.util.images;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ImagesFolderTest {

    @Test
    void testGetValue() {
        assertEquals("user", ImagesFolder.USER.getValue());
        assertEquals("plante", ImagesFolder.PLANTE.getValue());
        assertEquals("article", ImagesFolder.ARTICLE.getValue());
    }

    @Test
    void testFromValue_withValidValues() {
        // Vérification que la méthode fromValue retourne la bonne constante
        assertEquals(ImagesFolder.USER, ImagesFolder.fromValue("user"));
        assertEquals(ImagesFolder.PLANTE, ImagesFolder.fromValue("plante"));
        assertEquals(ImagesFolder.ARTICLE, ImagesFolder.fromValue("article"));
    }

    @Test
    void testFromValue_withInvalidValue() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> ImagesFolder.fromValue("unknown"));
        assertEquals("Unknown value: unknown", exception.getMessage());
    }

    @Test
    void testFromValue_caseInsensitive() {
        // Vérification que la méthode est insensible à la casse
        assertEquals(ImagesFolder.USER, ImagesFolder.fromValue("USER"));
        assertEquals(ImagesFolder.PLANTE, ImagesFolder.fromValue("PLANTE"));
        assertEquals(ImagesFolder.ARTICLE, ImagesFolder.fromValue("ARTICLE"));
    }
}
