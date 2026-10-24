package com.klee.system.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.klee.system.mapper.UserRoleMapper;
import com.klee.system.model.entity.UserRole;
import com.klee.system.service.UserRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserRoleServiceImpl extends ServiceImpl<UserRoleMapper, UserRole> implements UserRoleService {

  /**
   * 保存用户角色
   *
   * @param userId 用户ID
   * @param roleIds 选择的角色ID集合
   * @return
   */
  @Override
  public void saveUserRoles(Long userId, List<Long> roleIds) {
    if (userId == null || CollectionUtil.isEmpty(roleIds)) {
      return ;
    }

    // 获取现有角色
    List<Long> userRoleIds = this.list(new LambdaQueryWrapper<UserRole>()
        .select(UserRole::getRoleId)
        .eq(UserRole::getUserId, userId))
      .parallelStream()
      .map(UserRole::getRoleId)
      .toList();

    // 使用Set提升对比效率
    Set<Long> oldRoles = new HashSet<>(userRoleIds);
    Set<Long> newRoles = new HashSet<>(roleIds);

    // 计算变更集
    Set<Long> addedRoles = new HashSet<>(newRoles);
    addedRoles.removeAll(oldRoles);

    Set<Long> removedRoles = new HashSet<>(oldRoles);
    removedRoles.removeAll(newRoles);

    boolean rolesChanged = !CollectionUtils.isEmpty(addedRoles) || !CollectionUtils.isEmpty(removedRoles);

    // 批量保存新增角色
    if (!CollectionUtils.isEmpty(addedRoles)) {
      this.saveBatch(addedRoles.stream()
        .map(roleId -> new UserRole(userId, roleId))
        .collect(Collectors.toList()));
    }

    // 删除废弃角色
    if (!CollectionUtils.isEmpty(removedRoles)) {
      this.remove(new LambdaQueryWrapper<UserRole>()
        .eq(UserRole::getUserId, userId)
        .in(UserRole::getRoleId, removedRoles));
    }

    // 当权限变更时清除登录态
    if (rolesChanged) {
      Long currentUserId = userId;
      try { StpUtil.kickout(currentUserId); } catch (Exception ignored) {}
    }
  }

  /**
   * 判断角色是否存在绑定的用户
   *
   * @param roleId 角色ID
   * @return true：已分配 false：未分配
   */
  @Override
  public boolean hasAssignedUsers(Long roleId) {
    int count = this.baseMapper.countUsersForRole(roleId);
    return count > 0;
  }
}
