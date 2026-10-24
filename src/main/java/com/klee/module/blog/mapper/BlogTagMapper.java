package com.klee.module.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.klee.module.blog.model.entity.BlogTag;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface BlogTagMapper extends BaseMapper<BlogTag> {
}

