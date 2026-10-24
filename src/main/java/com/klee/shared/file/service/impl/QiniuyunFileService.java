package com.klee.shared.file.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.IdUtil;
import com.klee.core.security.util.SecurityUtils;
import com.klee.shared.file.enums.FileUsageTypeEnum;
import com.klee.shared.file.mapper.FileInfoMapper;
import com.klee.shared.file.model.FileInfo;
import com.klee.shared.file.service.FileService;
import com.qiniu.http.Response;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.Region;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;
import jakarta.annotation.Resource;
import lombok.Data;
import lombok.SneakyThrows;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.time.LocalDateTime;

/**
 * Aliyun 对象存储服务类
 *
 * @author haoxr
 * @since 2.3.0
 */
@Component
@ConditionalOnProperty(value = "oss.type", havingValue = "qiniuyun")
@ConfigurationProperties(prefix = "oss.qiniuyun")
@Data
public class QiniuyunFileService implements FileService {
    private String accessKey;
    private String secretKey;
    private String bucketName;
    private String domain; // 七牛云需要配置空间绑定的域名

    @Resource
    private FileInfoMapper fileInfoMapper;

    @Override
    @SneakyThrows
    public FileInfo uploadFile(MultipartFile file, String usageType) {
        // 1. 构造文件名
        String originalFilename = file.getOriginalFilename();
        String suffix = FileUtil.getSuffix(originalFilename);
        String fileName = DateUtil.format(LocalDateTime.now(), "yyyyMMdd") + "/" + IdUtil.simpleUUID() + "." + suffix;

        // 2. 七牛云上传逻辑
        Configuration cfg = new Configuration(Region.autoRegion()); // 自动识别区域
        UploadManager uploadManager = new UploadManager(cfg);
        Auth auth = Auth.create(accessKey, secretKey);
        String upToken = auth.uploadToken(bucketName);

        try (InputStream inputStream = file.getInputStream()) {
            Response response = uploadManager.put(inputStream, fileName, upToken, null, file.getContentType());
            if (!response.isOK()) {
                throw new RuntimeException("七牛云上传失败: " + response.error);
            }
        }

        // 3. 封装返回信息
        String fileUrl = domain.endsWith("/") ? domain + fileName : domain + "/" + fileName;

        FileInfo fi = new FileInfo();
        fi.setName(originalFilename);
        fi.setUrl(fileUrl);
        fi.setUsageType(FileUsageTypeEnum.normalize(usageType));
        fi.setSize(file.getSize());
        fi.setContentType(file.getContentType());
        fi.setUploaderUserId(SecurityUtils.getUserId());

        fileInfoMapper.insert(fi);
        return fi;
    }

    @Override
    @SneakyThrows
    public boolean deleteFile(String filePath) {
        Assert.notBlank(filePath, "删除文件路径不能为空");

        // 截取文件名 (Key)
        String fileName = filePath.replace(domain, "");
        if (fileName.startsWith("/")) {
            fileName = fileName.substring(1);
        }

        Configuration cfg = new Configuration(Region.autoRegion());
        Auth auth = Auth.create(accessKey, secretKey);
        BucketManager bucketManager = new BucketManager(auth, cfg);

        Response response = bucketManager.delete(bucketName, fileName);
        return response.isOK();
    }
}
