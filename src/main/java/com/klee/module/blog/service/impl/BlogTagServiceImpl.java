package com.klee.module.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.klee.common.enums.DeleteStatusEnum;
import com.klee.module.blog.converter.BlogConverter;
import com.klee.module.blog.enums.BlogPostStatusEnum;
import com.klee.module.blog.mapper.BlogPostMapper;
import com.klee.module.blog.mapper.BlogTagMapper;
import com.klee.module.blog.mapper.BlogPostTagMapper;
import com.klee.module.blog.model.entity.BlogTag;
import com.klee.module.blog.model.entity.BlogPostTag;
import com.klee.module.blog.model.entity.BlogPost;
import com.klee.module.blog.model.form.BlogTagForm;
import com.klee.module.blog.model.vo.BlogTagVO;
import com.klee.module.blog.service.BlogTagService;
import com.klee.module.blog.model.query.BlogTagPageQuery;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BlogTagServiceImpl extends ServiceImpl<BlogTagMapper, BlogTag> implements BlogTagService {

    private final BlogConverter converter;
    private final BlogPostTagMapper postTagMapper;
    private final BlogPostMapper blogPostMapper;

    @Override
    public boolean saveTag(BlogTagForm form) {
        BlogTag entity = converter.toTagEntity(form);
        entity.setId(null);
        entity.setIsDeleted(DeleteStatusEnum.NOT_DELETED.getValue());
        return this.save(entity);
    }

    @Override
    public boolean updateTag(Long id, BlogTagForm form) {
        BlogTag entity = converter.toTagEntity(form);
        entity.setId(id);
        entity.setIsDeleted(null); // keep
        return this.updateById(entity);
    }

    @Override
    public boolean deleteTag(Long id) {
        boolean tagDeleted = this.removeById(id);
        if (tagDeleted) {
            // 同步软删除标签关联关系
            postTagMapper.delete(new LambdaQueryWrapper<BlogPostTag>().eq(BlogPostTag::getTagId, id));
        }
        return tagDeleted;
    }

    @Override
    public IPage<BlogTagVO> pageTags(BlogTagPageQuery queryParams) {
        int pageNum = queryParams.getPageNum();
        int pageSize = queryParams.getPageSize();
        String keywords = queryParams.getKeywords();
        Integer status = queryParams.getStatus();

        Page<BlogTag> page = this.page(new Page<>(pageNum, pageSize),
                new LambdaQueryWrapper<BlogTag>()
                        .eq(BlogTag::getIsDeleted, DeleteStatusEnum.NOT_DELETED.getValue())
                        .like(StringUtils.isNotBlank(keywords), BlogTag::getName, keywords)
                        .eq(status != null, BlogTag::getStatus, status)
                        .orderByAsc(BlogTag::getSort)
                        .orderByDesc(BlogTag::getCreateTime)
        );

        List<BlogTag> records = page.getRecords();
        if (CollectionUtils.isEmpty(records)) {
            return new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        }
        List<Long> tagIds = records.stream().map(BlogTag::getId).toList();
        List<BlogPostTag> relations = postTagMapper.selectList(new LambdaQueryWrapper<BlogPostTag>()
                .in(BlogPostTag::getTagId, tagIds)
                .eq(BlogPostTag::getIsDeleted, DeleteStatusEnum.NOT_DELETED.getValue()));
        if (!CollectionUtils.isEmpty(relations)) {
            Set<Long> postIds = relations.stream().map(BlogPostTag::getPostId).collect(Collectors.toSet());
            if (!CollectionUtils.isEmpty(postIds)) {
                List<BlogPost> publishedPosts = blogPostMapper.selectList(new LambdaQueryWrapper<BlogPost>()
                        .in(BlogPost::getId, postIds)
                        .eq(BlogPost::getIsDeleted, DeleteStatusEnum.NOT_DELETED.getValue())
                        .eq(BlogPost::getStatus, BlogPostStatusEnum.PUBLISHED.getValue())
                        .select(BlogPost::getId));
                Set<Long> publishedPostIds = publishedPosts.stream().map(BlogPost::getId).collect(Collectors.toSet());
                relations = relations.stream()
                        .filter(r -> publishedPostIds.contains(r.getPostId()))
                        .toList();
            } else {
                relations = Collections.emptyList();
            }
        }
        Map<Long, Long> tagCountMap = relations.stream()
                .collect(Collectors.groupingBy(BlogPostTag::getTagId,
                        Collectors.mapping(BlogPostTag::getPostId, Collectors.toSet())))
                .entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> (long) e.getValue().size()));
        List<BlogTagVO> vos = converter.toTagVo(records);
        for (BlogTagVO vo : vos) {
            vo.setPostCount(tagCountMap.getOrDefault(vo.getId(), 0L));
        }
        Page<BlogTagVO> voPage = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        voPage.setRecords(vos);
        return voPage;
    }
}
