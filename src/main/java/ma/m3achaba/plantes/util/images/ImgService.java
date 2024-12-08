package ma.m3achaba.plantes.util.images;

import lombok.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;


@Service
public class ImgService {

    private static String uploadDirectory = "uploads";
    public Byte[] imageToByte(String filename) {
        return null;
    }
    public String saveImgas(MultipartFile file,ImagesFolder folder) {
        if (file.isEmpty()) {
            return "";
        }
        try {
            String originalFileName = file.getOriginalFilename(); // Get original file name
            String fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
            ensureFolderExists(folder.getValue());
            String fileName = folder.getValue()+"/"+ this.genrator(folder.getValue()) + fileExtension; // Generate random UUID with original file extension
            String filePath = Paths.get("").toAbsolutePath()+"/"+Paths.get(uploadDirectory)+"/" + fileName; // Construct file path using FOLDER_PATH
            File destFile = new File(filePath);
            file.transferTo(destFile);
            return fileName;
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }
    public String genrator(String folder) {
        UUID uuid = UUID.randomUUID();
        LocalDateTime dateTime = LocalDateTime.now();
        long secondsSinceEpoch = dateTime.toEpochSecond(ZoneOffset.UTC);
        String input = String.valueOf(secondsSinceEpoch);
        int midpoint = input.length() / 2;
        String firstPart = input.substring(0, midpoint);
        String secondPart = input.substring(midpoint);
        return firstPart+ uuid.toString()+folder+ secondPart;
    }
    private void ensureFolderExists(String folderPath) {
        try {
            if (!Files.exists(Paths.get(folderPath))) {
                Files.createDirectories(Paths.get(folderPath));
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to create directory: " + folderPath, e);
        }
    }
    public byte[] imageget(String fileName) throws IOException {
        return Files.readAllBytes(new File(Paths.get("").toAbsolutePath()+"/"+Paths.get(uploadDirectory)+"/"+fileName).toPath());
    }
}
