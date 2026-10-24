package com.klee.shared.file.service.impl;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import com.klee.core.security.util.SecurityUtils;
import com.klee.shared.file.enums.FileUsageTypeEnum; // 新增
import com.klee.shared.file.mapper.FileInfoMapper;
import com.klee.shared.file.model.FileInfo;
import com.klee.shared.file.service.FileService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.Resource; // 新增
import java.io.File;
import java.io.InputStream;
import java.time.LocalDateTime;

/**
 * 本地存储服务类
 *
 * @author Theo
 * @since 2024-12-09 17:11
 */
@Data
@Slf4j
@Component
@ConditionalOnProperty(value = "oss.type", havingValue = "local")
@ConfigurationProperties(prefix = "oss.local")
public class LocalFileService implements FileService {

    @Value("${oss.local.storage-path}")
    private String storagePath;

    @Resource
    private FileInfoMapper fileInfoMapper; // 改为非 final 字段，使用 @Resource 注入

    /**
     * 上传文件方法
     *
     * @param file 表单文件对象
     * @return 文件信息
     */
    @Override
    public FileInfo uploadFile(MultipartFile file, String usageType) {
        // 获取文件名
        String originalFilename = file.getOriginalFilename();
        // 获取文件后缀
        String suffix = FileUtil.getSuffix(originalFilename);
        // 生成uuid
        String fileName = IdUtil.simpleUUID() + "." + suffix; // uuid 文件名
        // 生成文件名(日期文件夹)
        String folder = DateUtil.format(LocalDateTime.now(), DatePattern.PURE_DATE_PATTERN);
        String filePrefix = storagePath.endsWith(File.separator) ? storagePath : storagePath + File.separator;
        //  try-with-resource 语法糖自动释放流
        try (InputStream inputStream = file.getInputStream()) {
            // 上传文件
            FileUtil.writeFromStream(inputStream, filePrefix + folder + File.separator + fileName);
        } catch (Exception e) {
            log.error("文件上传失败", e);
            throw new RuntimeException("文件上传失败");
        }
        // 获取文件访问路径，因为这里是本地存储，所以直接返回文件的相对路径，需要前端自行处理访问前缀
        String fileUrl = File.separator + folder + File.separator + fileName;
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


    /**
     * 删除文件
     * @param filePath 文件完整URL
     * @return 是否删除成功
     */
    @Override
    public boolean deleteFile(String filePath) {
        //判断文件是否为空
        if (filePath == null || filePath.isEmpty()) {
            return false;
        }
        // 判断filepath是否为文件夹
        if (FileUtil.isDirectory(storagePath + filePath)) {
            // 禁止删除文件夹
            return false;
        }
        // 删除文件
        return FileUtil.del(storagePath + filePath);
    }
}
