package com.example.demo.service.storage;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

/**
 * 本地磁盘存储实现。
 * <p>
 * 文件保存到 {@code storage.local.path} 指定的目录（默认项目根目录 uploads/），
 * 返回由 {@code storage.local.base-url} 与 {@code storage.local.url-prefix}
 * 拼接的完整访问 URL。
 */
@Service
@ConditionalOnProperty(prefix = "storage", name = "type", havingValue = "local", matchIfMissing = true)
public class LocalStorageService implements StorageService {

    private static final String DEFAULT_PATH = "uploads";

    @Value("${storage.local.path:uploads}")
    private String storagePath;

    @Value("${storage.local.base-url:http://localhost:8080}")
    private String baseUrl;

    @Value("${storage.local.url-prefix:/uploads}")
    private String urlPrefix;

    @Override
    public String upload(MultipartFile file) throws Exception {
        Path dir = resolveStorageDir();
        Files.createDirectories(dir);

        String ext = resolveExtension(file.getContentType());
        String filename = UUID.randomUUID().toString().replace("-", "") + ext;
        Files.write(dir.resolve(filename), file.getBytes());

        return buildUrl(filename);
    }

    private Path resolveStorageDir() {
        String path = StringUtils.hasText(storagePath) ? storagePath : DEFAULT_PATH;
        return Paths.get(path).toAbsolutePath().normalize();
    }

    private String resolveExtension(String contentType) {
        if (contentType == null) {
            return ".jpg";
        }
        return switch (contentType) {
            case "image/png" -> ".png";
            case "image/gif" -> ".gif";
            case "image/webp" -> ".webp";
            default -> ".jpg";
        };
    }

    private String buildUrl(String filename) {
        String base = StringUtils.trimTrailingCharacter(baseUrl, '/');
        String prefix = urlPrefix.startsWith("/") ? urlPrefix : "/" + urlPrefix;
        prefix = StringUtils.trimTrailingCharacter(prefix, '/');
        return base + prefix + "/" + filename;
    }
}
