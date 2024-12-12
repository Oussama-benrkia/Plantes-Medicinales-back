package ma.m3achaba.plantes.util.images;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;


@Service
@Slf4j
public class ImgService {

    private static final String UPLOADDIRECTORY = "uploads";
    public byte[] imageToByte(String filename) throws IOException {
        Path uploadDirectoryPath = Paths.get(System.getProperty("user.dir"), UPLOADDIRECTORY);
        Path filePath = uploadDirectoryPath.resolve(filename);
        return Files.readAllBytes(filePath);
    }
    public String addImage(MultipartFile file, ImagesFolder folder) {
        if (file == null || file.isEmpty()) {
            return "";
        }

        try {
            String originalFileName = file.getOriginalFilename();
            if (originalFileName == null) {
                throw new IllegalArgumentException("File name is null.");
            }
            String fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
            String uniqueFileName = this.genrator(folder.toString()) + fileExtension;
            Path uploadDirPath = Paths.get(UPLOADDIRECTORY).toAbsolutePath();
            Path folderPath = Paths.get(folder.getValue());
            Path filePath = uploadDirPath.resolve(folderPath).resolve(uniqueFileName);
            Files.createDirectories(filePath.getParent());
            file.transferTo(filePath.toFile());
            return folder.getValue() + "/" + uniqueFileName;
        } catch (IOException e) {
            log.error("Error saving file: {}", file.getOriginalFilename(), e);
            return ""; // Return empty string on failure
        } catch (IllegalArgumentException e) {
            log.error("Invalid file name: {}", e.getMessage());
            return "";
        }
    }
    public Boolean deleteImage(String fileName) {
        Path filePath = Paths.get(System.getProperty("user.dir"), UPLOADDIRECTORY, fileName); // Dynamically construct file path
        try {
            if (Files.exists(filePath)) {
                Files.delete(filePath); // Use Files.delete to remove the file
                return true;
            }
        } catch (IOException e) {
            log.error("Error deleting file: {}", filePath, e); // Log error
        }
        return false;
    }
    private String genrator(String folder) {
        UUID uuid = UUID.randomUUID();
        LocalDateTime dateTime = LocalDateTime.now();
        long secondsSinceEpoch = dateTime.toEpochSecond(ZoneOffset.UTC);
        String input = String.valueOf(secondsSinceEpoch);
        int midpoint = input.length() / 2;
        String firstPart = input.substring(0, midpoint);
        String secondPart = input.substring(midpoint);
        return firstPart+ uuid+folder+ secondPart;
    }
}
