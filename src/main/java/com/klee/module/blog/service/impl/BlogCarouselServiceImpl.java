package com.klee.module.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.klee.common.enums.DeleteStatusEnum;
import com.klee.core.security.util.SecurityUtils;
import com.klee.shared.file.mapper.FileInfoMapper;
import com.klee.shared.file.model.FileInfo;
import com.klee.module.blog.converter.BlogCarouselConverter;
import com.klee.module.blog.mapper.BlogCarouselMapper;
import com.klee.module.blog.model.entity.BlogCarousel;
import com.klee.module.blog.model.form.BlogCarouselForm;
import com.klee.module.blog.model.query.BlogCarouselPageQuery;
import com.klee.module.blog.model.vo.BlogCarouselVO;
import com.klee.module.blog.service.BlogCarouselService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BlogCarouselServiceImpl extends ServiceImpl<BlogCarouselMapper, BlogCarousel> implements BlogCarouselService {

    private final BlogCarouselConverter converter;
    private final FileInfoMapper fileInfoMapper;

    @Override
    public boolean saveCarousel(BlogCarouselForm form) {
        BlogCarousel entity = converter.toEntity(form);
        entity.setId(null);
        entity.setCreateBy(SecurityUtils.getUserId());
        entity.setIsDeleted(DeleteStatusEnum.NOT_DELETED.getValue());
        return this.save(entity);
    }

    @Override
    public boolean updateCarousel(Long id, BlogCarouselForm form) {
        BlogCarousel entity = converter.toEntity(form);
        entity.setId(id);
        entity.setUpdateBy(SecurityUtils.getUserId());
        entity.setIsDeleted(null); // keep existing
        return this.updateById(entity);
    }

    @Override
    public boolean deleteCarousel(Long id) {
        return this.removeById(id);
    }

    @Override
    public IPage<BlogCarouselVO> pageCarousels(BlogCarouselPageQuery queryParams) {
        Page<BlogCarousel> page = this.page(new Page<>(queryParams.getPageNum(), queryParams.getPageSize()),
                new LambdaQueryWrapper<BlogCarousel>()
                        .eq(BlogCarousel::getIsDeleted, DeleteStatusEnum.NOT_DELETED.getValue())
                        .orderByDesc(BlogCarousel::getCreateTime)
        );
        Page<BlogCarouselVO> voPage = converter.toVo(page);
        enrichFileUrl(voPage.getRecords());
        return voPage;
    }

    @Override
    public List<BlogCarouselVO> listEnabled() {
        List<BlogCarousel> list = this.list(new LambdaQueryWrapper<BlogCarousel>()
                .eq(BlogCarousel::getIsDeleted, DeleteStatusEnum.NOT_DELETED.getValue())
                .orderByDesc(BlogCarousel::getCreateTime)
        );
        List<BlogCarouselVO> vos = list.stream().map(converter::toVo).toList();
        enrichFileUrl(vos);
        return vos;
    }

    private void enrichFileUrl(List<BlogCarouselVO> list) {
        if (list == null || list.isEmpty()) return;
        Set<Long> fileIds = list.stream().map(BlogCarouselVO::getFileId).filter(Objects::nonNull).collect(Collectors.toSet());
        if (fileIds.isEmpty()) return;
        List<FileInfo> files = fileInfoMapper.selectList(new LambdaQueryWrapper<FileInfo>().in(FileInfo::getId, fileIds));
        Map<Long, String> idUrlMap = files.stream().collect(Collectors.toMap(FileInfo::getId, FileInfo::getUrl));
        list.forEach(vo -> vo.setFileUrl(idUrlMap.get(vo.getFileId())));
    }
}

