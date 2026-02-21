package com.medical.smartmedicine.file.service;

import com.medical.smartmedicine.file.vo.FileUploadVO;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件服务接口
 *
 * @author Smart Medicine Team
 * @since 1.0.0
 */
public interface FileService {

    /**
     * 上传文件
     *
     * @param file 文件
     * @return 文件上传结果
     */
    FileUploadVO upload(MultipartFile file);
    
    /**
     * 上传文件(别名)
     */
    FileUploadVO uploadFile(MultipartFile file);
    
    /**
     * 上传图片
     */
    FileUploadVO uploadImage(MultipartFile file);

    /**
     * 删除文件
     *
     * @param fileUrl 文件URL
     * @return 是否成功
     */
    boolean delete(String fileUrl);
    
    /**
     * 删除文件(别名)
     */
    boolean deleteFile(String fileUrl);
}
