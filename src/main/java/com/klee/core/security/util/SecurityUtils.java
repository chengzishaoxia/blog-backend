package com.klee.core.security.util;

import cn.dev33.satoken.exception.SaTokenContextException;
import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.klee.common.constant.SystemConstants;

import java.util.*;

/**
 * 安全工具（Sa-Token版本）
 */
public class SecurityUtils {


    /**
     * 获取用户ID
     *
     * @return Long
     */
    public static Long getUserId() {
        try {
            Object id = StpUtil.getLoginIdDefaultNull();
            if (id == null) return null;
            if (id instanceof Long l) return l;
            return Long.parseLong(id.toString());
        } catch (SaTokenContextException e) {
            return null; // 启动/非Web上下文场景返回null
        } catch (Exception e) {
            return null;
        }
    }


    /**
     * 获取用户账号
     *
     * @return String 用户账号
     */
    public static String getUsername() {
        SaSession session = safeSession();
        return session == null ? null : (String) session.get("username");
    }


    /**
     * 获取部门ID
     *
     * @return Long
     */
    public static Long getDeptId() {
        SaSession session = safeSession();
        return session == null ? null : castLong(session.get("deptId"));
    }

    /**
     * 获取数据权限范围
     *
     * @return Integer
     */
    public static Integer getDataScope() {
        SaSession session = safeSession();
        Object v = session == null ? null : session.get("dataScope");
        return v == null ? null : Integer.parseInt(v.toString());
    }


    /**
     * 获取角色集合
     *
     * @return 角色集合
     */
    public static Set<String> getRoles() {
        try {
            List<String> list = StpUtil.getRoleList();
            return CollectionUtil.isEmpty(list) ? Collections.emptySet() : new HashSet<>(list);
        } catch (Exception e) {
            return Collections.emptySet();
        }
    }

    /**
     * 是否超级管理员
     * <p>
     * 超级管理员忽视任何权限判断
     */
    public static boolean isRoot() {
        try {
            return getRoles().contains(SystemConstants.ROOT_ROLE_CODE);
        } catch (Exception e) {
            return false;
        }
    }


    private static SaSession safeSession() {
        try { return StpUtil.getSession(); } catch (Exception e) { return null; }
    }

    private static Long castLong(Object o) {
        if (o == null) return null;
        if (o instanceof Long l) return l;
        return Long.parseLong(o.toString());
    }
}
