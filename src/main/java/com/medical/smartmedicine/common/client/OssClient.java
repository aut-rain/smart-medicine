package com.medical.smartmedicine.common.client;

import cn.hutool.core.util.IdUtil;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.CannedAccessControlList;
import com.aliyun.oss.model.CreateBucketRequest;
import com.aliyun.oss.model.OSSObject;
import com.aliyun.oss.model.PutObjectRequest;
import com.aliyun.oss.model.PutObjectResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * 阿里云OSS对象存储客户端
 * 提供文件上传、删除等功能
 *
 * @author Smart Medicine Team
 * @since 1.0.0
 */
@Slf4j
@Component
public class OssClient {

    @Value("${oss.bucket-name:}")
    private String bucketName;

    @Value("${oss.end-point:}")
    private String endPoint;

    @Value("${oss.access-key:}")
    private String accessKeyId;

    @Value("${oss.access-secret:}")
    private String accessKeySecret;

    /**
     * 上传文件到OSS
     *
     * @param file 文件
     * @param path 存储路径
     * @return 文件访问URL
     * @throws IOException IO异常
     */
    public String upload(MultipartFile file, String path) throws IOException {
        if (file == null || path == null) {
            log.warn("文件或路径为空，上传失败");
            return null;
        }

        OSSClient ossClient = null;
        try {
            ossClient = new OSSClient(endPoint, accessKeyId, accessKeySecret);
            
            // 如果Bucket不存在则创建
            if (!ossClient.doesBucketExist(bucketName)) {
                log.info("创建Bucket: {}", bucketName);
                CreateBucketRequest createBucketRequest = new CreateBucketRequest(bucketName);
                createBucketRequest.setCannedACL(CannedAccessControlList.PublicRead);
                ossClient.createBucket(createBucketRequest);
            }

            String extension = getFileExtension(file);
            String fileUrl = path + "/" + IdUtil.simpleUUID() + extension;
            String url = "https://" + bucketName + "." + endPoint + "/" + fileUrl;

            // 上传文件
            PutObjectResult result = ossClient.putObject(
                    new PutObjectRequest(bucketName, fileUrl, file.getInputStream())
            );
            ossClient.setBucketAcl(bucketName, CannedAccessControlList.PublicRead);

            log.info("文件上传成功: {}", url);
            return url;
            
        } finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }
    }

    /**
     * 删除文件
     *
     * @param fileUrl 文件URL
     * @return 是否删除成功
     */
    public boolean delete(String fileUrl) {
        if (fileUrl == null || fileUrl.isEmpty()) {
            log.warn("文件URL为空，删除失败");
            return false;
        }

        OSSClient ossClient = null;
        try {
            ossClient = new OSSClient(endPoint, accessKeyId, accessKeySecret);
            
            // 从URL中提取ObjectKey
            // URL格式: https://bucket-name.endpoint/path/to/file.ext
            String objectKey = extractObjectKey(fileUrl);
            
            if (objectKey == null || objectKey.isEmpty()) {
                log.warn("无法从URL提取ObjectKey: {}", fileUrl);
                return false;
            }

            // 删除文件
            ossClient.deleteObject(bucketName, objectKey);
            log.info("文件删除成功: {}", fileUrl);
            return true;
            
        } catch (Exception e) {
            log.error("文件删除失败: {}", fileUrl, e);
            return false;
        } finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }
    }

    /**
     * 从完整URL中提取ObjectKey
     * 
     * @param fileUrl 完整的文件URL
     * @return ObjectKey (文件在OSS中的路径)
     */
    private String extractObjectKey(String fileUrl) {
        try {
            // URL格式: https://bucket-name.endpoint/path/to/file.ext
            String prefix = "https://" + bucketName + "." + endPoint + "/";
            if (fileUrl.startsWith(prefix)) {
                return fileUrl.substring(prefix.length());
            }
            
            // 如果不是完整URL，可能直接就是ObjectKey
            if (!fileUrl.startsWith("http")) {
                return fileUrl;
            }
            
            log.warn("无法解析URL: {}", fileUrl);
            return null;
        } catch (Exception e) {
            log.error("解析URL失败: {}", fileUrl, e);
            return null;
        }
    }

    /**
     * 获取文件的扩展名
     *
     * @param file 文件
     * @return 扩展名（包含点）
     */
    public static String getFileExtension(MultipartFile file) {
        String filename = file.getOriginalFilename();
        if (filename == null || !filename.contains(".")) {
            return "";
        }
        return filename.substring(filename.lastIndexOf("."));
    }

    /**
     * 构建完整的OSS URL
     *
     * @param ossPath OSS路径（如: smart-medicine/news/covers/xxx.jpg）
     * @return 完整URL（如: https://bucket.endpoint/path）
     */
    public String buildUrl(String ossPath) {
        if (ossPath == null || ossPath.isEmpty()) {
            return "";
        }
        // 如果已经是完整URL，直接返回
        if (ossPath.startsWith("http")) {
            return ossPath;
        }
        // 拼接完整URL
        return "https://" + bucketName + "." + endPoint + "/" + ossPath;
    }

    /**
     * 从OSS读取文件内容
     *
     * @param ossPath OSS路径或完整URL
     * @return 文件内容字符串
     */
    public String readFileContent(String ossPath) {
        if (ossPath == null || ossPath.isEmpty()) {
            log.warn("OSS路径为空");
            return null;
        }

        OSSClient ossClient = null;
        try {
            ossClient = new OSSClient(endPoint, accessKeyId, accessKeySecret);

            // 提取ObjectKey
            String objectKey = extractObjectKey(ossPath);
            if (objectKey == null || objectKey.isEmpty()) {
                log.warn("无法从URL提取ObjectKey: {}", ossPath);
                return null;
            }

            // 读取文件内容
            OSSObject ossObject = ossClient.getObject(bucketName, objectKey);
            try (InputStream inputStream = ossObject.getObjectContent();
                 BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {

                StringBuilder content = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    content.append(line).append("\n");
                }

                log.info("文件读取成功: {}", ossPath);
                return content.toString();
            }

        } catch (Exception e) {
            log.error("读取OSS文件失败: {}", ossPath, e);
            return null;
        } finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }
    }
}
