package com.example.drone.security;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * DroneRealm 单元测试.
 * <p>
 * 测试自定义 Realm 的认证和授权逻辑，包括：
 * <ul>
 *   <li>管理员用户认证</li>
 *   <li>普通用户认证</li>
 *   <li>不存在用户认证失败</li>
 *   <li>管理员角色授权</li>
 *   <li>普通用户角色授权</li>
 * </ul>
 * </p>
 *
 * @author Drone Management System
 * @version 1.0.0
 * @since 2024-01-15
 */
class DroneRealmTest {

    private DroneRealm droneRealm;

    @BeforeEach
    void setUp() {
        droneRealm = new DroneRealm();
    }

    /**
     * 测试管理员用户认证成功.
     */
    @Test
    void testAuthenticateAdminUser_Success() {
        // 准备测试数据
        UsernamePasswordToken token = new UsernamePasswordToken("admin", "admin123");

        // 执行认证
        AuthenticationInfo authInfo = droneRealm.doGetAuthenticationInfo(token);

        // 验证结果
        assertNotNull(authInfo, "认证信息不应该为空");
        assertEquals("admin", authInfo.getPrincipals().getPrimaryPrincipal(),
                "用户名应该是 admin");
        assertEquals("admin123", authInfo.getCredentials(),
                "密码应该是 admin123");
    }

    /**
     * 测试普通用户认证成功.
     */
    @Test
    void testAuthenticateNormalUser_Success() {
        // 准备测试数据
        UsernamePasswordToken token = new UsernamePasswordToken("user", "user123");

        // 执行认证
        AuthenticationInfo authInfo = droneRealm.doGetAuthenticationInfo(token);

        // 验证结果
        assertNotNull(authInfo, "认证信息不应该为空");
        assertEquals("user", authInfo.getPrincipals().getPrimaryPrincipal(),
                "用户名应该是 user");
        assertEquals("user123", authInfo.getCredentials(),
                "密码应该是 user123");
    }

    /**
     * 测试不存在的用户认证失败.
     */
    @Test
    void testAuthenticateNonExistentUser_ThrowsException() {
        // 准备测试数据
        UsernamePasswordToken token = new UsernamePasswordToken("nonexistent", "password");

        // 执行认证并验证抛出异常
        assertThrows(AuthenticationException.class, () -> {
            droneRealm.doGetAuthenticationInfo(token);
        }, "不存在的用户应该抛出 AuthenticationException");
    }

    /**
     * 测试管理员用户授权.
     */
    @Test
    void testAuthorizeAdminUser() {
        // 准备测试数据
        PrincipalCollection principals = new SimplePrincipalCollection("admin", "droneRealm");

        // 执行授权
        AuthorizationInfo authzInfo = droneRealm.doGetAuthorizationInfo(principals);

        // 验证结果
        assertNotNull(authzInfo, "授权信息不应该为空");
        assertNotNull(authzInfo.getRoles(), "角色集合不应该为空");
        assertEquals(1, authzInfo.getRoles().size(), "应该有 1 个角色");
        assertTrue(authzInfo.getRoles().contains("admin"), "应该包含 admin 角色");
    }

    /**
     * 测试普通用户授权.
     */
    @Test
    void testAuthorizeNormalUser() {
        // 准备测试数据
        PrincipalCollection principals = new SimplePrincipalCollection("user", "droneRealm");

        // 执行授权
        AuthorizationInfo authzInfo = droneRealm.doGetAuthorizationInfo(principals);

        // 验证结果
        assertNotNull(authzInfo, "授权信息不应该为空");
        assertNotNull(authzInfo.getRoles(), "角色集合不应该为空");
        assertEquals(1, authzInfo.getRoles().size(), "应该有 1 个角色");
        assertTrue(authzInfo.getRoles().contains("user"), "应该包含 user 角色");
    }

    /**
     * 测试未知用户授权（无角色）.
     */
    @Test
    void testAuthorizeUnknownUser_NoRoles() {
        // 准备测试数据
        PrincipalCollection principals = new SimplePrincipalCollection("unknown", "droneRealm");

        // 执行授权
        AuthorizationInfo authzInfo = droneRealm.doGetAuthorizationInfo(principals);

        // 验证结果
        assertNotNull(authzInfo, "授权信息不应该为空");
        assertNotNull(authzInfo.getRoles(), "角色集合不应该为空");
        assertEquals(0, authzInfo.getRoles().size(), "未知用户应该没有角色");
    }
}
