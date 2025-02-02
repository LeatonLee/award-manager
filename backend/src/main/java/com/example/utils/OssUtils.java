package com.example.utils;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.UUID;

@Component
public class OssUtils {

    @Value("${aliyun.oss.endpoint}")
    private String endpoint;

    @Value("${aliyun.oss.accessKeyId}")
    private String accessKeyId;

    @Value("${aliyun.oss.accessKeySecret}")
    private String accessKeySecret;

    @Value("${aliyun.oss.bucketName}")
    private String bucketName;

    @Value("${aliyun.oss.filePath}")
    private String filePath;

    public String uploadFile(InputStream inputStream, String originalFilename) {
        String fileName = UUID.randomUUID().toString().replaceAll("-", "") + "_" + originalFilename;
        String objectName = filePath + fileName;

        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
        try {
            ossClient.putObject(bucketName, objectName, inputStream);
        } finally {
            ossClient.shutdown();
        }

        return "https://" + bucketName + ".oss-cn-beijing.aliyuncs.com/" + objectName;
    }
}