package com.example.demo.controller;

import com.example.demo.common.BusinessException;
import com.example.demo.common.Result;
import com.example.demo.util.OssUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/api/upload")
public class UploadController {

    private static final Set<String> ALLOWED = Set.of("image/jpeg", "image/png", "image/gif", "image/webp");

    @Value("${app.upload-dir:uploads}")
    private String uploadDir;

    private final OssUtil ossUtil;

    public UploadController(OssUtil ossUtil) {
        this.ossUtil = ossUtil;
    }

    @PostMapping
    public Result<String> upload(@RequestParam("file") MultipartFile file, HttpServletRequest request) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new BusinessException("请选择图片文件");
        }
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED.contains(contentType)) {
            throw new BusinessException("仅支持 JPG/PNG/GIF/WEBP 格式图片");
        }
        if (file.getSize() > 5 * 1024 * 1024) {
            throw new BusinessException("单张图片不能超过5MB");
        }

        if (ossUtil.isEnabled()) {
            try {
                String url = ossUtil.upload(file);
                return Result.ok(url);
            } catch (Exception e) {
                e.printStackTrace();
                throw new BusinessException("OSS上传失败：" + e.getMessage() + "，请检查OSS配置或Bucket权限");
            }
        }

        Path dir = Paths.get(uploadDir).toAbsolutePath();
        Files.createDirectories(dir);
        String ext = switch (contentType) {
            case "image/png" -> ".png";
            case "image/gif" -> ".gif";
            case "image/webp" -> ".webp";
            default -> ".jpg";
        };
        String filename = UUID.randomUUID() + ext;
        Files.write(dir.resolve(filename), file.getBytes());
        String baseUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
        return Result.ok(baseUrl + "/uploads/" + filename);
    }
}
