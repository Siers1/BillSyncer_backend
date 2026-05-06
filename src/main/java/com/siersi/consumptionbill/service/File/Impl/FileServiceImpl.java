package com.siersi.consumptionbill.service.File.Impl;

import com.aliyun.oss.OSS;
import com.siersi.consumptionbill.config.OssConfig;
import com.siersi.consumptionbill.service.File.FileService;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {
    private final OSS ossClient;
    private final OssConfig ossConfig;
    
    @Override
    public String uploadImage(MultipartFile file) {
        // 文件大小校验
        if (file.getSize() > 10 * 1024 * 1024) {
            throw new RuntimeException("文件大小不能超过10MB");
        }

        // 文件名校验
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || !originalFilename.contains(".")) {
            throw new RuntimeException("文件名无效");
        }

        // 文件类型校验
        String extension = originalFilename.substring(originalFilename.lastIndexOf(".")).toLowerCase();
        if (!extension.matches("\\.(jpg|jpeg|png|gif|bmp|webp)")) {
            throw new RuntimeException("只支持图片格式: jpg, jpeg, png, gif, bmp, webp");
        }
        
        try {
            // 生成唯一文件名
            String fileName = UUID.randomUUID() + extension;
            
            // 上传到OSS
            ossClient.putObject(ossConfig.getBucketName(), fileName, file.getInputStream());
            
            // 返回完整的文件URL
            return ossConfig.getUrlPrefix() + fileName;
        } catch (IOException e) {
            throw new RuntimeException("文件上传失败", e);
        }
    }
}
