package com.klee.core.security.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

/**
 * 用户详情（Sa-Token迁移后简化版本）
 */
@Data
@NoArgsConstructor
public class SysUserDetails {

    private Long userId;
    private String username;
    private String password;
    private Boolean enabled;
    private Long deptId;
    private Integer dataScope;
    private Set<String> roles; // 角色集合

    public SysUserDetails(UserAuthCredentials user) {
        this.userId = user.getUserId();
        this.username = user.getUsername();
        this.password = user.getPassword();
        this.enabled = user.getStatus() != null && user.getStatus() == 1;
        this.deptId = user.getDeptId();
        this.dataScope = user.getDataScope();
        this.roles = user.getRoles();
    }
}
