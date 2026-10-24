package com.klee.shared.file.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.IdUtil;
import com.amazonaws.ClientConfiguration;
import com.amazonaws.Protocol;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.klee.core.security.util.SecurityUtils;
import com.klee.shared.file.enums.FileUsageTypeEnum;
import com.klee.shared.file.mapper.FileInfoMapper;
import com.klee.shared.file.model.FileInfo;
import com.klee.shared.file.service.FileService;
import jakarta.annotation.PostConstruct;
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
 * 京东云 OSS 对象存储服务类 (兼容 S3 协议实现)
 *
 * @author haoxr
 * @since 2.3.0
 */
@Component
@ConditionalOnProperty(value = "oss.type", havingValue = "jingdongyun")
@ConfigurationProperties(prefix = "oss.jingdongyun")
@Data
public class JingdongyunFileService implements FileService {

    /**
     * 服务内网 Endpoint (例如: s3-internal.cn-north-1.jdcloud-oss.com)
     * 仅用于后端服务器与 OSS 之间的内网传输
     */
    private String endpoint;

    /**
     * 外网访问域名 (例如: https://bucketName.s3.cn-north-1.jdcloud-oss.com)
     * 用于拼接最终返回给前端的 URL，也支持以后换成你自己备案的 CDN 自定义域名
     */
    private String domain;

    /**
     * 区域 (例如: cn-north-1)
     */
    private String region;
    /**
     * Access Key
     */
    private String accessKey;
    /**
     * Secret Key
     */
    private String secretKey;
    /**
     * 存储桶名称
     */
    private String bucketName;

    private AmazonS3 s3Client;

    @Resource
    private FileInfoMapper fileInfoMapper;

    @PostConstruct
    public void init() {
        // 配置客户端连接参数
        ClientConfiguration clientConfig = new ClientConfiguration();
        clientConfig.setProtocol(Protocol.HTTPS); // 如果内网不支持 https，这里可能需要改成 Protocol.HTTP

        // 初始化 S3 客户端 (底层连接走内网 endpoint)
        s3Client = AmazonS3ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(accessKey, secretKey)))
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(endpoint, region))
                .withClientConfiguration(clientConfig)
                .build();
    }

    @Override
    @SneakyThrows
    public FileInfo uploadFile(MultipartFile file, String usageType) {
        // 生成文件名
        String originalFilename = file.getOriginalFilename();
        String suffix = FileUtil.getSuffix(originalFilename);
        String fileName = DateUtil.format(LocalDateTime.now(), "yyyyMMdd") + "/" + IdUtil.simpleUUID() + "." + suffix;

        try (InputStream inputStream = file.getInputStream()) {
            // 设置元数据
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(file.getContentType());
            metadata.setContentLength(file.getSize());

            // 上传请求 (此处流量会通过 init() 中配置的内网 endpoint 走)
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, fileName, inputStream, metadata);
            s3Client.putObject(putObjectRequest);
        } catch (Exception e) {
            throw new RuntimeException("京东云文件上传失败", e);
        }

        // 拼接访问路径 (使用外网域名 domain)
        // 兼容 domain 配置结尾是否带了斜杠
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
    public boolean deleteFile(String filePath) {
        Assert.notBlank(filePath, "删除文件路径不能为空");

        // 提取 Key (文件名)
        // 基于外部访问域名把前面的前缀截取掉，只保留真实的文件 key
        String prefix = domain.endsWith("/") ? domain : domain + "/";
        String key = filePath.replace(prefix, "");

        // 删除文件同样走内网请求
        s3Client.deleteObject(bucketName, key);
        return true;
    }
}