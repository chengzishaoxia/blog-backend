package com.klee.core.security.satoken;

import cn.dev33.satoken.stp.StpInterface;
import cn.dev33.satoken.stp.StpUtil;
import com.klee.core.security.service.PermissionService;
import com.klee.system.enums.RoleCodeEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.*;

@Component
@RequiredArgsConstructor
public class SaStpInterfaceImpl implements StpInterface {

    private final PermissionService permissionService;

    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        Set<String> roles = new HashSet<>(getRoleList(loginId, loginType));
        if (CollectionUtils.isEmpty(roles)) return Collections.emptyList();
        if (roles.contains(RoleCodeEnum.ROOT.getCode())) {
            return Collections.singletonList("*");
        }
        return new ArrayList<>(permissionService.getRolePermsFormCache(roles));
    }

    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        try {
            Object rolesObj = StpUtil.getSessionByLoginId(loginId).get("roles");
            if (rolesObj instanceof Collection<?> col) {
                List<String> list = new ArrayList<>();
                for (Object o : col) list.add(String.valueOf(o));
                return list;
            }
        } catch (Exception ignored) {}
        return Collections.emptyList();
    }
}
