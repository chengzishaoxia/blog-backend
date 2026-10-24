package com.klee.system.converter;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.klee.system.model.entity.Dict;
import com.klee.system.model.vo.DictPageVO;
import com.klee.system.model.form.DictForm;
import org.mapstruct.Mapper;

/**
 * 字典 对象转换器
 *
 * @author Ray Hao
 * @since 2022/6/8
 */
@Mapper(componentModel = "spring")
public interface DictConverter {

    Page<DictPageVO> toPageVo(Page<Dict> page);

    DictForm toForm(Dict entity);

    Dict toEntity(DictForm entity);
}
