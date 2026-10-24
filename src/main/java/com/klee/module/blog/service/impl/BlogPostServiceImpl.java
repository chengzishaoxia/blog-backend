package com.klee.module.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.klee.common.enums.DeleteStatusEnum;
import com.klee.core.security.util.SecurityUtils;
import com.klee.module.blog.converter.BlogConverter;
import com.klee.module.blog.enums.BlogPostStatusEnum;
import com.klee.module.blog.mapper.BlogPostMapper;
import com.klee.module.blog.mapper.BlogPostTagMapper;
import com.klee.module.blog.mapper.BlogTagMapper;
import com.klee.module.blog.model.entity.BlogPost;
import com.klee.module.blog.model.entity.BlogPostTag;
import com.klee.module.blog.model.entity.BlogTag;
import com.klee.module.blog.model.form.BlogPostForm;
import com.klee.module.blog.model.query.BlogPostPageQuery;
import com.klee.module.blog.model.vo.BlogArchiveGroupVO;
import com.klee.module.blog.model.vo.BlogArchivePostVO;
import com.klee.module.blog.model.vo.BlogPostVO;
import com.klee.module.blog.model.vo.BlogTagVO;
import com.klee.module.blog.service.BlogPostService;
import com.klee.shared.file.mapper.FileInfoMapper;
import com.klee.shared.file.model.FileInfo;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class BlogPostServiceImpl extends ServiceImpl<BlogPostMapper, BlogPost> implements BlogPostService {

    private final BlogConverter converter;
    private final BlogTagMapper tagMapper;
    private final BlogPostTagMapper postTagMapper;
    @Resource
    private FileInfoMapper fileInfoMapper; // 注入文件信息

    @Override
    public IPage<BlogPostVO> page(BlogPostPageQuery query) {
        Page<BlogPost> page = new Page<>(query.getPageNum(), query.getPageSize());
        LambdaQueryWrapper<BlogPost> qw = new LambdaQueryWrapper<>();
        qw.eq(BlogPost::getIsDeleted, DeleteStatusEnum.NOT_DELETED.getValue());
        if (StringUtils.isNotBlank(query.getKeywords())) {
            qw.like(BlogPost::getTitle, query.getKeywords());
        }
        if (query.getStatus() != null) {
            qw.eq(BlogPost::getStatus, query.getStatus());
        }
        if (query.getTagId() != null) {
            List<BlogPostTag> pts = postTagMapper.selectList(new LambdaQueryWrapper<BlogPostTag>().eq(BlogPostTag::getTagId, query.getTagId()));
            if (CollectionUtils.isEmpty(pts)) {
                return new Page<>();
            }
            qw.in(BlogPost::getId, pts.stream().map(BlogPostTag::getPostId).collect(Collectors.toSet()));
        }
        // 置顶优先，其次按创建时间倒序；补充 id 倒序保证分页排序稳定
        qw.orderByDesc(BlogPost::getPinned)
            .orderByDesc(BlogPost::getCreateTime)
            .orderByDesc(BlogPost::getId);
        Page<BlogPost> postPage = this.page(page, qw);
        Page<BlogPostVO> voPage = new Page<>(postPage.getCurrent(), postPage.getSize(), postPage.getTotal());
        List<BlogPostVO> voList = enrichWithTags(postPage.getRecords());
        voPage.setRecords(voList);
        return voPage;
    }

    @Override
    public BlogPostVO getPost(Long id, boolean increaseView) {
        BlogPost post = this.getById(id);
        if (post == null || Objects.equals(post.getIsDeleted(), DeleteStatusEnum.DELETED.getValue())) return null;
        if (increaseView) {
            this.update(new LambdaUpdateWrapper<BlogPost>().eq(BlogPost::getId, id).setSql("view_count = IFNULL(view_count,0)+1"));
            post.setViewCount(Optional.ofNullable(post.getViewCount()).orElse(0L) + 1);
        }
        return enrichWithTags(Collections.singletonList(post)).get(0);
    }

    @Override
    public List<BlogPostVO> recommendList() {
        LambdaQueryWrapper<BlogPost> qw = new LambdaQueryWrapper<>();
        qw.eq(BlogPost::getIsDeleted, DeleteStatusEnum.NOT_DELETED.getValue())
                .eq(BlogPost::getStatus, BlogPostStatusEnum.PUBLISHED.getValue())
                .eq(BlogPost::getIsRecommend, 1)
                .orderByDesc(BlogPost::getPinned)
                .orderByDesc(BlogPost::getCreateTime)
            .orderByDesc(BlogPost::getId)
                .last("limit 5");
        return enrichWithTags(this.list(qw));
    }

    @Override
    @Transactional
    public boolean savePost(BlogPostForm form) {
        BlogPost entity = converter.toPostEntity(form);
        entity.setId(null);
        entity.setAuthorId(SecurityUtils.getUserId());
        entity.setViewCount(0L);
        entity.setIsDeleted(DeleteStatusEnum.NOT_DELETED.getValue());
        if (entity.getPinned() == null) {
            entity.setPinned(0); // 默认未置顶
        }
        if (entity.getIsRecommend() == null) {
            entity.setIsRecommend(0);
        }
        boolean ok = this.save(entity);
        if (ok) {
            savePostTags(entity.getId(), form.getTagIds());
        }
        return ok;
    }

    @Override
    @Transactional
    public boolean updatePost(Long id, BlogPostForm form) {
        BlogPost entity = converter.toPostEntity(form);
        entity.setId(id);
        entity.setIsDeleted(null); // do not override
        if (entity.getPinned() == null) {
            entity.setPinned(0); // 防止传空覆盖
        }
        if (entity.getIsRecommend() == null) {
            entity.setIsRecommend(0);
        }
        boolean ok = this.updateById(entity);
        if (ok) {
            // 使用增量更新(UPsert)方式维护标签关联
            savePostTags(id, form.getTagIds());
        }
        return ok;
    }

    @Override
    @Transactional
    public boolean deletePost(Long id) {
        if (id == null) return false;
        return this.update(new LambdaUpdateWrapper<BlogPost>().eq(BlogPost::getId, id).set(BlogPost::getIsDeleted, DeleteStatusEnum.DELETED.getValue()));
    }

    @Override
    public boolean upsertPost(BlogPostForm form) {
        if (form == null) return false;
        return form.getId() == null ? savePost(form) : updatePost(form.getId(), form);
    }

    @Override
    public List<BlogArchiveGroupVO> listArchives() {
        LambdaQueryWrapper<BlogPost> qw = new LambdaQueryWrapper<>();
        qw.eq(BlogPost::getIsDeleted, DeleteStatusEnum.NOT_DELETED.getValue())
          .eq(BlogPost::getStatus, BlogPostStatusEnum.PUBLISHED.getValue()) // 仅发布文章进入归档
          .select(BlogPost::getId, BlogPost::getTitle, BlogPost::getCreateTime);
        // 最近三年时间下限 (含当前时间往前推 3 年)
        LocalDateTime threeYearsAgo = LocalDateTime.now().minusYears(3);
        qw.ge(BlogPost::getCreateTime, threeYearsAgo)
          .orderByDesc(BlogPost::getCreateTime);
        List<BlogPost> posts = this.list(qw);
        if (CollectionUtils.isEmpty(posts)) return Collections.emptyList();
        // 分组 key: year-month
        Map<String, List<BlogPost>> grouped = posts.stream().collect(Collectors.groupingBy(p -> {
            if (p.getCreateTime() == null) return "unknown";
            return p.getCreateTime().getYear() + "-" + String.format("%02d", p.getCreateTime().getMonthValue());
        }));
        List<BlogArchiveGroupVO> groups = new ArrayList<>();
        grouped.forEach((ym, list) -> {
            if ("unknown".equals(ym)) return; // 跳过无时间
            String[] arr = ym.split("-");
            BlogArchiveGroupVO g = new BlogArchiveGroupVO();
            g.setYear(Integer.parseInt(arr[0]));
            g.setMonth(Integer.parseInt(arr[1]));
            List<BlogArchivePostVO> pv = list.stream().sorted(Comparator.comparing(BlogPost::getCreateTime).reversed()).map(p -> {
                BlogArchivePostVO v = new BlogArchivePostVO();
                v.setId(p.getId());
                v.setTitle(p.getTitle());
                v.setCreateTime(p.getCreateTime());
                return v;
            }).collect(Collectors.toList());
            g.setPosts(pv);
            g.setCount(pv.size());
            groups.add(g);
        });
        // 按年份倒序, 月份倒序
        groups.sort(Comparator.comparing(BlogArchiveGroupVO::getYear).reversed()
                .thenComparing(Comparator.comparing(BlogArchiveGroupVO::getMonth).reversed()));
        return groups;
    }

    private void savePostTags(Long postId, List<Long> tagIds) {
        if (postId == null) return;
        // 目标标签集合(允许为空表示清空)
        Set<Long> newTagSet = (tagIds == null) ? Collections.emptySet() : new HashSet<>(tagIds);
        // 查询该文章现有(包含已软删)关联
        List<BlogPostTag> existing = postTagMapper.selectList(new LambdaQueryWrapper<BlogPostTag>().eq(BlogPostTag::getPostId, postId));
        Map<Long, BlogPostTag> byTag = existing.stream().collect(Collectors.toMap(BlogPostTag::getTagId, e -> e, (a, b) -> a));
        Set<Long> activeTagIds = existing.stream().filter(e -> Objects.equals(e.getIsDeleted(), DeleteStatusEnum.NOT_DELETED.getValue())).map(BlogPostTag::getTagId).collect(Collectors.toSet());

        // 1. 新增或激活需要的标签
        for (Long tagId : newTagSet) {
            BlogPostTag rel = byTag.get(tagId);
            if (rel == null) {
                BlogPostTag pt = new BlogPostTag();
                pt.setPostId(postId);
                pt.setTagId(tagId);
                pt.setIsDeleted(DeleteStatusEnum.NOT_DELETED.getValue());
                postTagMapper.insert(pt);
            }
        }

        // 2. 软删不再需要的标签
        for (Long tagId : activeTagIds) {
            if (!newTagSet.contains(tagId)) {
                BlogPostTag rel = byTag.get(tagId);
                if (rel != null && !Objects.equals(rel.getIsDeleted(), DeleteStatusEnum.DELETED.getValue())) {
                    postTagMapper.deleteById(rel);
                }
            }
        }
    }

    private List<BlogPostVO> enrichWithTags(List<BlogPost> posts) {
        if (CollectionUtils.isEmpty(posts)) return Collections.emptyList();
        // 先转换基本VO
        List<BlogPostVO> list = new ArrayList<>();
        for (BlogPost p : posts) {
            BlogPostVO vo = converter.toPostVo(p);
            // 设置文件ID
            vo.setCoverImageFileId(p.getCoverImageFileId());
            vo.setPinned(p.getPinned()); // 映射 pinned
            vo.setIsRecommend(p.getIsRecommend());
            list.add(vo);
        }
        // 标签处理
        List<Long> postIds = posts.stream().map(BlogPost::getId).toList();
        List<BlogPostTag> relations = postTagMapper.selectList(new LambdaQueryWrapper<BlogPostTag>().in(BlogPostTag::getPostId, postIds));
        Map<Long, List<Long>> postTagIdsMap = relations.stream().collect(Collectors.groupingBy(BlogPostTag::getPostId, Collectors.mapping(BlogPostTag::getTagId, Collectors.toList())));
        Set<Long> allTagIds = relations.stream().map(BlogPostTag::getTagId).collect(Collectors.toSet());
        Map<Long, BlogTagVO> tagVoMap = new HashMap<>();
        if (!CollectionUtils.isEmpty(allTagIds)) {
            List<BlogTag> tags = tagMapper.selectBatchIds(allTagIds);
            List<BlogTagVO> tagVos = converter.toTagVo(tags);
            tagVoMap = tagVos.stream().collect(Collectors.toMap(BlogTagVO::getId, t -> t));
        }
        // 文件URL填充
        List<Long> coverFileIds = posts.stream().map(BlogPost::getCoverImageFileId).filter(Objects::nonNull).distinct().toList();
        Map<Long, String> fileUrlMap = coverFileIds.isEmpty() ? Map.of() : fileInfoMapper.selectBatchIds(coverFileIds)
                .stream().collect(Collectors.toMap(FileInfo::getId, FileInfo::getUrl));
        for (BlogPostVO vo : list) {
            List<Long> tids = postTagIdsMap.getOrDefault(vo.getId(), Collections.emptyList());
            vo.setTags(tids.stream().map(tagVoMap::get).filter(Objects::nonNull).collect(Collectors.toList()));
            if (vo.getCoverImageFileId() != null) {
                vo.setCoverImage(fileUrlMap.get(vo.getCoverImageFileId()));
            }
        }
        return list;
    }
}
