package ua.prozoryvit.transparency.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Set;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ua.prozoryvit.transparency.config.AppProperties;

@Service
public class FileStorageService {

    private static final Set<String> ALLOWED_EXTENSIONS = Set.of("pdf", "jpg", "jpeg", "png");
    private static final long MAX_SIZE = 5 * 1024 * 1024;

    private final Path uploadRoot;

    public FileStorageService(AppProperties appProperties) throws IOException {
        this.uploadRoot = Path.of(appProperties.uploadDir()).toAbsolutePath().normalize();
        Files.createDirectories(uploadRoot);
    }

    public StoredFile store(MultipartFile file, Long campaignId, Long reportId) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Файл порожній");
        }
        if (file.getSize() > MAX_SIZE) {
            throw new IllegalArgumentException("Файл перевищує 5 МБ");
        }
        String original = file.getOriginalFilename();
        String ext = extension(original);
        if (!ALLOWED_EXTENSIONS.contains(ext)) {
            throw new IllegalArgumentException("Дозволені формати: pdf, jpg, png");
        }

        String storedName = campaignId + "/" + reportId + "/" + UUID.randomUUID() + "." + ext;
        Path target = uploadRoot.resolve(storedName);
        Files.createDirectories(target.getParent());
        Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);

        return new StoredFile(original, storedName, file.getContentType(), file.getSize());
    }

    public Path resolve(String storagePath) {
        Path resolved = uploadRoot.resolve(storagePath).normalize();
        if (!resolved.startsWith(uploadRoot)) {
            throw new IllegalArgumentException("Недопустимий шлях");
        }
        return resolved;
    }

    private String extension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "";
        }
        return filename.substring(filename.lastIndexOf('.') + 1).toLowerCase();
    }

    public record StoredFile(String originalFilename, String storagePath, String mimeType, long size) {
    }
}
