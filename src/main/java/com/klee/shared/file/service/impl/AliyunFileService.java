package com.klee.shared.file.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.IdUtil;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.ObjectMetadata;
import com.aliyun.oss.model.PutObjectRequest;
import com.klee.shared.file.service.FileService;
import com.klee.shared.file.model.FileInfo;
import com.klee.shared.file.mapper.FileInfoMapper;
import com.klee.core.security.util.SecurityUtils;
import com.klee.shared.file.enums.FileUsageTypeEnum; // 新增
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource; // 新增
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
@ConditionalOnProperty(value = "oss.type", havingValue = "aliyun")
@ConfigurationProperties(prefix = "oss.aliyun")
@Data
public class AliyunFileService implements FileService {
    /**
     * 服务Endpoint
     */
    private String endpoint;
    /**
     * 访问凭据
     */
    private String accessKeyId;
    /**
     * 凭据密钥
     */
    private String accessKeySecret;
    /**
     * 存储桶名称
     */
    private String bucketName;

    private OSS aliyunOssClient;

    @Resource
    private FileInfoMapper fileInfoMapper; // 非 final 注入

    @PostConstruct
    public void init() {
        aliyunOssClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
    }

    @Override
    @SneakyThrows
    public FileInfo uploadFile(MultipartFile file, String usageType) {

        // 获取文件名称
        String originalFilename = file.getOriginalFilename();
        // 生成文件名(日期文件夹)
        String suffix = FileUtil.getSuffix(originalFilename);
        String uuid = IdUtil.simpleUUID();
        String fileName = DateUtil.format(LocalDateTime.now(), "yyyyMMdd") + "/" + uuid + "." + suffix;
        //  try-with-resource 语法糖自动释放流
        try (InputStream inputStream = file.getInputStream()) {

            // 设置上传文件的元信息，例如Content-Type
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(file.getContentType());
            // 创建PutObjectRequest对象，指定Bucket名称、对象名称和输入流
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, fileName, inputStream, metadata);
            // 上传文件
            aliyunOssClient.putObject(putObjectRequest);
        } catch (Exception e) {
            throw new RuntimeException("文件上传失败");
        }
        // 获取文件访问路径
        String fileUrl = "https://" + bucketName + "." + endpoint + "/" + fileName;
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
    public boolean deleteFile(String filePath) {
        Assert.notBlank(filePath, "删除文件路径不能为空");
        String fileHost = "https://" + bucketName + "." + endpoint; // 文件主机域名
        String fileName = filePath.substring(fileHost.length() + 1); // +1 是/占一个字符，截断左闭右开
        aliyunOssClient.deleteObject(bucketName, fileName);
        return true;
    }
}
