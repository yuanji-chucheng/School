package com.example.demo.service.storage;

import com.example.demo.util.OssUtil;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * 阿里云 OSS 存储实现。
 * <p>
 * 仅在 {@code storage.type=oss} 时装配，内部委托原有 {@link OssUtil}
 * 完成上传，相关参数仍由 {@code aliyun.oss.*} 配置。
 */
@Service
@ConditionalOnProperty(prefix = "storage", name = "type", havingValue = "oss")
public class OssStorageService implements StorageService {

    private final OssUtil ossUtil;

    public OssStorageService(OssUtil ossUtil) {
        this.ossUtil = ossUtil;
    }

    @Override
    public String upload(MultipartFile file) throws Exception {
        return ossUtil.upload(file);
    }
}
