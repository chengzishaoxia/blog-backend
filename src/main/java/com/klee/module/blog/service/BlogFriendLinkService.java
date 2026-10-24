package com.klee.module.blog.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.klee.module.blog.model.entity.BlogFriendLink;
import com.klee.module.blog.model.form.BlogFriendLinkForm;
import com.klee.module.blog.model.query.BlogFriendLinkPageQuery;
import com.klee.module.blog.model.vo.BlogFriendLinkVO;

import java.util.List;

public interface BlogFriendLinkService extends IService<BlogFriendLink> {

    IPage<BlogFriendLinkVO> pageFriendLinks(BlogFriendLinkPageQuery query);

    boolean saveFriendLink(BlogFriendLinkForm form);

    boolean updateFriendLink(Long id, BlogFriendLinkForm form);

    boolean deleteFriendLink(Long id);

    List<BlogFriendLinkVO> listEnabled();
}
