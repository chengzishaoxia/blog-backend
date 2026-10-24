package com.klee.module.blog.converter;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.klee.module.blog.model.entity.BlogFriendLink;
import com.klee.module.blog.model.entity.BlogPost;
import com.klee.module.blog.model.entity.BlogTag;
import com.klee.module.blog.model.entity.BlogComment;
import com.klee.module.blog.model.entity.BlogMessage;
import com.klee.module.blog.model.form.BlogFriendLinkForm;
import com.klee.module.blog.model.form.BlogPostForm;
import com.klee.module.blog.model.form.BlogTagForm;
import com.klee.module.blog.model.form.BlogCommentForm;
import com.klee.module.blog.model.form.BlogMessageForm;
import com.klee.module.blog.model.vo.BlogFriendLinkVO;
import com.klee.module.blog.model.vo.BlogPostVO;
import com.klee.module.blog.model.vo.BlogTagVO;
import com.klee.module.blog.model.vo.BlogCommentVO;
import com.klee.module.blog.model.vo.BlogMessageDanmakuVO;
import com.klee.module.blog.model.vo.BlogMessageVO;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.List;

@Mapper(componentModel = "spring")
public interface BlogConverter {

    @Mappings({
            @Mapping(target = "coverImage", ignore = true), // URL will be filled manually
            @Mapping(target = "coverImageFileId", ignore = true) // fileId set manually in service
    })
    BlogPostVO toPostVo(BlogPost entity);
    Page<BlogPostVO> toPostVo(Page<BlogPost> page);

    BlogPostForm toPostForm(BlogPost entity);
    @InheritInverseConfiguration(name = "toPostForm")
    BlogPost toPostEntity(BlogPostForm form);

    BlogTagVO toTagVo(BlogTag entity);
    List<BlogTagVO> toTagVo(List<BlogTag> list);

    BlogFriendLinkVO toFriendLinkVo(BlogFriendLink entity);
    List<BlogFriendLinkVO> toFriendLinkVo(List<BlogFriendLink> list);

    BlogFriendLinkForm toFriendLinkForm(BlogFriendLink entity);
    @InheritInverseConfiguration(name = "toFriendLinkForm")
    BlogFriendLink toFriendLinkEntity(BlogFriendLinkForm form);

    BlogTagForm toTagForm(BlogTag entity);
    @InheritInverseConfiguration(name = "toTagForm")
    BlogTag toTagEntity(BlogTagForm form);

    BlogCommentVO toCommentVo(BlogComment entity);
    List<BlogCommentVO> toCommentVo(List<BlogComment> list);

    BlogCommentForm toCommentForm(BlogComment entity);
    @InheritInverseConfiguration(name = "toCommentForm")
    BlogComment toCommentEntity(BlogCommentForm form);

    BlogMessageVO toMessageVo(BlogMessage entity);
    List<BlogMessageVO> toMessageVo(List<BlogMessage> list);

    BlogMessageDanmakuVO toMessageDanmakuVo(BlogMessage entity);
    List<BlogMessageDanmakuVO> toMessageDanmakuVo(List<BlogMessage> list);

    BlogMessageForm toMessageForm(BlogMessage entity);
    @InheritInverseConfiguration(name = "toMessageForm")
    BlogMessage toMessageEntity(BlogMessageForm form);
}
