package com.klee.module.blog.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.klee.common.base.BaseEntity;
import lombok.Getter;
import lombok.Setter;

/**
 * 博客友链
 */
@Getter
@Setter
@TableName("blog_friend_link")
public class BlogFriendLink extends BaseEntity {

    private String name;
    private String url;
    private String avatar;
    private String description;
    private Integer sort;
    private Integer status;
    private Integer isDeleted;
}
