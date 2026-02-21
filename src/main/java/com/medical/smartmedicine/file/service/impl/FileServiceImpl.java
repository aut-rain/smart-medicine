package com.medical.smartmedicine.file.service.impl;

import com.medical.smartmedicine.common.client.OssClient;
import com.medical.smartmedicine.common.enums.ErrorCodeEnum;
import com.medical.smartmedicine.common.exception.BusinessException;
import com.medical.smartmedicine.file.service.FileService;
import com.medical.smartmedicine.file.vo.FileUploadVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件服务实现类
 *
 * @author Smart Medicine Team
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {

    private final OssClient ossClient;

    @Override
    public FileUploadVO upload(MultipartFile file) {
        return uploadToOss(file, "smart-medicine/files");
    }
    
    @Override
    public FileUploadVO uploadFile(MultipartFile file) {
        return uploadToOss(file, "smart-medicine/files");
    }
    
    @Override
    public FileUploadVO uploadImage(MultipartFile file) {
        return uploadToOss(file, "smart-medicine/images");
    }

    /**
     * 通用的OSS上传方法
     *
     * @param file 文件
     * @param path OSS存储路径
     * @return 上传结果
     */
    private FileUploadVO uploadToOss(MultipartFile file, String path) {
        try {
            log.info("开始上传文件: fileName={}, size={}, path={}", 
                    file.getOriginalFilename(), file.getSize(), path);
            
            // 使用OSS上传文件
            String url = ossClient.upload(file, path);
            
            if (url == null || url.isEmpty()) {
                log.error("文件上传失败: OSS返回空URL");
                throw new BusinessException(ErrorCodeEnum.FILE_UPLOAD_FAILED);
            }
            
            FileUploadVO uploadVO = FileUploadVO.builder()
                    .url(url)
                    .fileName(file.getOriginalFilename())
                    .fileSize(file.getSize())
                    .fileType(file.getContentType())
                    .uploadTime(System.currentTimeMillis())
                    .build();
            
            log.info("文件上传成功: url={}", url);
            return uploadVO;
            
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("文件上传异常: {}", file.getOriginalFilename(), e);
            throw new BusinessException(ErrorCodeEnum.FILE_UPLOAD_FAILED, "文件上传失败: " + e.getMessage());
        }
    }

    @Override
    public boolean delete(String fileUrl) {
        try {
            log.info("删除文件: fileUrl={}", fileUrl);
            boolean result = ossClient.delete(fileUrl);
            if (result) {
                log.info("文件删除成功: {}", fileUrl);
            } else {
                log.warn("文件删除失败: {}", fileUrl);
            }
            return result;
        } catch (Exception e) {
            log.error("文件删除异常: {}", fileUrl, e);
            return false;
        }
    }
    
    @Override
    public boolean deleteFile(String fileUrl) {
        return delete(fileUrl);
    }
}
