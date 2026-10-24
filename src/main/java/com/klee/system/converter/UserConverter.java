package com.klee.system.converter;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.klee.common.model.Option;
import com.klee.system.model.entity.User;
import com.klee.system.model.dto.CurrentUserDTO;
import com.klee.system.model.vo.UserPageVO;
import com.klee.system.model.vo.UserProfileVO;
import com.klee.system.model.bo.UserBO;
import com.klee.system.model.form.UserForm;
import com.klee.system.model.dto.UserImportDTO;
import com.klee.system.model.form.UserProfileForm;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.List;

/**
 * 用户对象转换器
 *
 * @author Ray.Hao
 * @since 2022/6/8
 */
@Mapper(componentModel = "spring")
public interface UserConverter {

    @Mappings({
            @Mapping(target = "avatar", ignore = true), // URL 后端填充
            @Mapping(target = "avatarFileId", source = "avatarFileId")
    })
    UserPageVO toPageVo(UserBO bo);

    Page<UserPageVO> toPageVo(Page<UserBO> bo);

    UserForm toForm(User entity);

    @InheritInverseConfiguration(name = "toForm")
    User toEntity(UserForm entity);

    @Mappings({
            @Mapping(target = "userId", source = "id"),
            @Mapping(target = "avatar", ignore = true),
            @Mapping(target = "avatarFileId", source = "avatarFileId")
    })
    CurrentUserDTO toCurrentUserDto(User entity);

    User toEntity(UserImportDTO vo);

    @Mappings({
            @Mapping(target = "avatar", ignore = true),
            @Mapping(target = "avatarFileId", source = "avatarFileId")
    })
    UserProfileVO toProfileVo(UserBO bo);

    User toEntity(UserProfileForm formData);

    @Mappings({
            @Mapping(target = "label", source = "nickname"),
            @Mapping(target = "value", source = "id")
    })
    Option<String> toOption(User entity);

    List<Option<String>> toOptions(List<User> list);
}
