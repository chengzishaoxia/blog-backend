package com.klee.module.chat.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.klee.module.chat.mapper.ChatEmojiMapper;
import com.klee.module.chat.model.entity.ChatEmoji;
import com.klee.module.chat.service.ChatEmojiService;
import com.klee.shared.file.model.FileInfo;
import com.klee.shared.file.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.util.Assert;
import org.springframework.web.multipart.MultipartFile;
import com.klee.common.exception.BusinessException;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.security.MessageDigest;
import java.util.HexFormat;

@Service
@RequiredArgsConstructor
public class ChatEmojiServiceImpl extends ServiceImpl<ChatEmojiMapper, ChatEmoji> implements ChatEmojiService {

    private final FileService fileService;

    private String calcMd5(MultipartFile file) {
        if (file == null || file.isEmpty()) return null;
        try (InputStream is = file.getInputStream()) {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] buf = new byte[8192];
            int n;
            while ((n = is.read(buf)) > 0) md.update(buf, 0, n);
            return HexFormat.of().formatHex(md.digest());
        } catch (Exception e) {
            return null;
        }
    }

    private void fillImageSize(ChatEmoji entity, MultipartFile file) {
        if (file == null || file.isEmpty()) return;
        try {
            BufferedImage img = ImageIO.read(file.getInputStream());
            if (img != null) {
                entity.setWidth(img.getWidth());
                entity.setHeight(img.getHeight());
            }
        } catch (Exception ignored) {
        }
    }

    @Override
    public Long addPersonalEmoji(Long userId, MultipartFile file) {
        Assert.notNull(userId, "userId不能为空");
        if (file == null || file.isEmpty()) throw new BusinessException("FILE_EMPTY");
        String md5 = calcMd5(file);
        if (md5 != null) {
            ChatEmoji dup = lambdaQuery().eq(ChatEmoji::getUserId, userId).eq(ChatEmoji::getIsDeleted, 0).eq(ChatEmoji::getMd5, md5).one();
            if (dup != null) throw new BusinessException("DUPLICATE_IMAGE");
        }
        FileInfo fi = fileService.uploadFile(file, "EMOJI");
        if (fi == null || fi.getId() == null) throw new BusinessException("UPLOAD_FAIL");
        ChatEmoji entity = new ChatEmoji();
        entity.setUserId(userId);
        entity.setFileId(fi.getId());
        entity.setMd5(md5);
        fillImageSize(entity, file);
        try {
            save(entity);
        } catch (DuplicateKeyException e) {
            throw new BusinessException("DUPLICATE_IMAGE");
        }
        return entity.getId();
    }


    @Override
    public void deletePersonalEmoji(Long userId, Long emojiId) {
        Assert.notNull(userId, "userId不能为空");
        ChatEmoji entity = getById(emojiId);
        if (entity == null || entity.getIsDeleted() != 0 || entity.getUserId() == null || !entity.getUserId().equals(userId)) {
            throw new BusinessException("NOT_FOUND_OR_DENIED");
        }
        removeById(emojiId); // 逻辑删除
    }

}
