package com.example.demo.service.storage;

import org.springframework.web.multipart.MultipartFile;

/**
 * 文件存储服务抽象。
 * <p>
 * 通过配置项 {@code storage.type} 切换具体实现：
 * <ul>
 *     <li>local：本地磁盘存储（默认），见 {@link LocalStorageService}</li>
 *     <li>oss：阿里云 OSS 存储，见 {@link OssStorageService}</li>
 * </ul>
 */
public interface StorageService {

    /**
     * 上传文件并返回可访问的 URL。
     *
     * @param file 已通过校验的上传文件
     * @return 文件访问 URL（完整地址）
     * @throws Exception 上传过程中发生的异常
     */
    String upload(MultipartFile file) throws Exception;
}
