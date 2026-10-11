package com.example.demo.controller;

import com.example.demo.common.BusinessException;
import com.example.demo.common.Result;
import com.example.demo.service.storage.StorageService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;

/** 上传图片 **/
@RestController
@RequestMapping("/api/upload")
public class UploadController {

    private static final Set<String> ALLOWED = Set.of("image/jpeg", "image/png", "image/gif", "image/webp");

    private static final long MAX_SIZE = 5L * 1024 * 1024;

    private final StorageService storageService;

    public UploadController(StorageService storageService) {
        this.storageService = storageService;
    }

    @PostMapping
    public Result<String> upload(@RequestParam("file") MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException("请选择图片文件");
        }
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED.contains(contentType)) {
            throw new BusinessException("仅支持 JPG/PNG/GIF/WEBP 格式图片");
        }
        if (file.getSize() > MAX_SIZE) {
            throw new BusinessException("单张图片不能超过5MB");
        }

        try {
            return Result.ok(storageService.upload(file));
        } catch (Exception e) {
            e.printStackTrace();
            throw new BusinessException("文件上传失败：" + e.getMessage());
        }
    }
}
