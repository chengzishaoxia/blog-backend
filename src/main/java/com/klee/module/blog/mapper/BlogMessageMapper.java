package com.klee.module.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.klee.module.blog.model.entity.BlogMessage;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface BlogMessageMapper extends BaseMapper<BlogMessage> {
}
