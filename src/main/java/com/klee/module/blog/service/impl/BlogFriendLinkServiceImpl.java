package com.klee.module.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.klee.common.enums.DeleteStatusEnum;
import com.klee.module.blog.converter.BlogConverter;
import com.klee.module.blog.mapper.BlogFriendLinkMapper;
import com.klee.module.blog.model.entity.BlogFriendLink;
import com.klee.module.blog.model.form.BlogFriendLinkForm;
import com.klee.module.blog.model.query.BlogFriendLinkPageQuery;
import com.klee.module.blog.model.vo.BlogFriendLinkVO;
import com.klee.module.blog.service.BlogFriendLinkService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BlogFriendLinkServiceImpl extends ServiceImpl<BlogFriendLinkMapper, BlogFriendLink> implements BlogFriendLinkService {

    private final BlogConverter converter;

    @Override
    public IPage<BlogFriendLinkVO> pageFriendLinks(BlogFriendLinkPageQuery query) {
        Page<BlogFriendLink> page = this.page(new Page<>(query.getPageNum(), query.getPageSize()),
                new LambdaQueryWrapper<BlogFriendLink>()
                        .eq(BlogFriendLink::getIsDeleted, DeleteStatusEnum.NOT_DELETED.getValue())
                        .and(StringUtils.isNotBlank(query.getKeywords()), wrapper -> wrapper
                                .like(BlogFriendLink::getName, query.getKeywords())
                                .or()
                                .like(BlogFriendLink::getDescription, query.getKeywords()))
                        .eq(query.getStatus() != null, BlogFriendLink::getStatus, query.getStatus())
                        .orderByAsc(BlogFriendLink::getSort)
                        .orderByDesc(BlogFriendLink::getCreateTime));

        Page<BlogFriendLinkVO> voPage = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        voPage.setRecords(converter.toFriendLinkVo(page.getRecords()));
        return voPage;
    }

    @Override
    public boolean saveFriendLink(BlogFriendLinkForm form) {
        BlogFriendLink entity = converter.toFriendLinkEntity(form);
        entity.setId(null);
        entity.setIsDeleted(DeleteStatusEnum.NOT_DELETED.getValue());
        return this.save(entity);
    }

    @Override
    public boolean updateFriendLink(Long id, BlogFriendLinkForm form) {
        BlogFriendLink entity = converter.toFriendLinkEntity(form);
        entity.setId(id);
        entity.setIsDeleted(null);
        return this.updateById(entity);
    }

    @Override
    public boolean deleteFriendLink(Long id) {
        return this.removeById(id);
    }

    @Override
    public List<BlogFriendLinkVO> listEnabled() {
        List<BlogFriendLink> list = this.list(new LambdaQueryWrapper<BlogFriendLink>()
                .eq(BlogFriendLink::getIsDeleted, DeleteStatusEnum.NOT_DELETED.getValue())
                .eq(BlogFriendLink::getStatus, 1)
                .orderByAsc(BlogFriendLink::getSort)
                .orderByDesc(BlogFriendLink::getCreateTime));
        return converter.toFriendLinkVo(list);
    }
}
