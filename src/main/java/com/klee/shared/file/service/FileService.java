package com.klee.shared.file.service;

import com.klee.shared.file.model.FileInfo;
import org.springframework.web.multipart.MultipartFile;

/**
 * 对象存储服务接口层
 *
 * @author haoxr
 * @since 2022/11/19
 */
public interface FileService {

    /**
     * 上传文件
     * @param file 表单文件对象
     * @param usageType 使用类型(如: AVATAR, EMOJI, BLOG_COVER, CAROUSEL, OTHER)
     * @return 文件信息(持久化后含ID)
     */
    FileInfo uploadFile(MultipartFile file, String usageType);

    /**
     * 删除文件
     *
     * @param filePath 文件完整URL
     * @return 删除结果
     */
    boolean deleteFile(String filePath);


}
