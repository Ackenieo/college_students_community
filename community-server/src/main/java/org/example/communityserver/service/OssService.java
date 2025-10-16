package org.example.communityserver.service;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import org.example.communityserver.config.OssProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Service
public class OssService implements DisposableBean {

    private static final Logger logger = LoggerFactory.getLogger(OssService.class);

    @Autowired
    private OssProperties ossProperties;

    private OSS client;

    private OSS getClient() {
        if (client == null && ossProperties.isEnabled()) {
            client = new OSSClientBuilder().build(
                    ossProperties.getEndpoint(),
                    ossProperties.getAccessKeyId(),
                    ossProperties.getAccessKeySecret());
        }
        return client;
    }

    public String uploadPostObject(String originalFilename, InputStream data, long size, String contentType) {
        if (!ossProperties.isEnabled()) {
            throw new IllegalStateException("OSS 未启用");
        }
        if (size > ossProperties.getMaxSizeBytes()) {
            throw new IllegalArgumentException("文件过大");
        }
        String datePath = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        String key = ossProperties.getBasePath() + datePath + "/" + UUID.randomUUID() + 
                normalizeSuffix(originalFilename);
        try {
            getClient().putObject(ossProperties.getBucket(), key, data);
            logger.info("OSS 上传成功, key={}", key);
            return key;
        } catch (Exception e) {
            logger.error("OSS 上传失败", e);
            throw e;
        }
    }

    private String normalizeSuffix(String originalFilename) {
        if (originalFilename == null) return "";
        int i = originalFilename.lastIndexOf('.');
        return i >= 0 ? originalFilename.substring(i) : "";
    }

    @Override
    public void destroy() {
        if (client != null) {
            client.shutdown();
        }
    }
}


