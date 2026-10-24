package com.klee.module.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.klee.module.blog.model.entity.BlogPostTag;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface BlogPostTagMapper extends BaseMapper<BlogPostTag> {
}

