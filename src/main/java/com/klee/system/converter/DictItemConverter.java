package com.klee.system.converter;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.klee.system.model.entity.DictItem;
import com.klee.system.model.form.DictItemForm;
import com.klee.system.model.vo.DictPageVO;
import com.klee.common.model.Option;
import org.mapstruct.Mapper;

import java.util.List;

/**
 * 字典项对象转换器
 *
 * @author Ray.Hao
 * @since 2022/6/8
 */
@Mapper(componentModel = "spring")
public interface DictItemConverter {

    Page<DictPageVO> toPageVo(Page<DictItem> page);

    DictItemForm toForm(DictItem entity);

    DictItem toEntity(DictItemForm formFata);

    Option<Long> toOption(DictItem dictItem);
    List<Option<Long>> toOption(List<DictItem> dictData);
}
