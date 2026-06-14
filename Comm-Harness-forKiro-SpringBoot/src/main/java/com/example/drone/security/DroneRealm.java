package com.example.drone.security;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

/**
 * 自定义 Shiro Realm，负责认证和授权逻辑.
 * <p>
 * 功能包括：
 * <ul>
 *   <li>认证（Authentication）：验证用户身份</li>
 *   <li>授权（Authorization）：验证用户权限和角色</li>
 * </ul>
 * </p>
 * <p>
 * 当前实现使用硬编码用户数据（用于演示和测试）：
 * <ul>
 *   <li>管理员用户：admin / admin123（角色：admin）</li>
 *   <li>普通用户：user / user123（角色：user）</li>
 * </ul>
 * </p>
 * <p>
 * 生产环境建议：
 * <ul>
 *   <li>从数据库加载用户信息</li>
 *   <li>使用加密算法存储密码（如 BCrypt、SHA-256）</li>
 *   <li>实现用户管理功能</li>
 * </ul>
 * </p>
 * <p>
 * 关联需求：
 * <ul>
 *   <li>需求 2：查询需要认证</li>
 *   <li>需求 1/3/4：创建/更新/删除需要管理员权限</li>
 * </ul>
 * </p>
 *
 * @author Drone Management System
 * @version 1.0.0
 * @since 2024-01-15
 */
@Component
public class DroneRealm extends AuthorizingRealm {

    private static final Logger logger = LoggerFactory.getLogger(DroneRealm.class);

    /**
     * 授权逻辑：获取用户的角色和权限信息.
     * <p>
     * 当用户访问需要特定角色或权限的资源时，Shiro 会调用此方法。
     * </p>
     * <p>
     * 角色定义：
     * <ul>
     *   <li>admin：管理员角色，可以执行所有操作（创建、查询、更新、删除）</li>
     *   <li>user：普通用户角色，只能执行查询操作</li>
     * </ul>
     * </p>
     *
     * @param principals 用户身份信息（包含用户名）
     * @return AuthorizationInfo 授权信息（角色和权限）
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        String username = (String) principals.getPrimaryPrincipal();
        logger.debug("执行授权逻辑，用户名: {}", username);

        SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();
        Set<String> roles = new HashSet<>();

        // 根据用户名分配角色（硬编码，生产环境应从数据库查询）
        if ("admin".equals(username)) {
            roles.add("admin");
            logger.debug("用户 {} 拥有管理员角色", username);
        } else if ("user".equals(username)) {
            roles.add("user");
            logger.debug("用户 {} 拥有普通用户角色", username);
        }

        authorizationInfo.setRoles(roles);
        logger.info("授权完成，用户: {}, 角色: {}", username, roles);
        return authorizationInfo;
    }

    /**
     * 认证逻辑：验证用户身份.
     * <p>
     * 当用户登录时，Shiro 会调用此方法验证用户名和密码。
     * </p>
     * <p>
     * 硬编码用户数据（仅用于演示）：
     * <ul>
     *   <li>用户名: admin, 密码: admin123, 角色: admin</li>
     *   <li>用户名: user, 密码: user123, 角色: user</li>
     * </ul>
     * </p>
     * <p>
     * 生产环境建议：
     * <ul>
     *   <li>从数据库查询用户信息</li>
     *   <li>使用加密算法验证密码（如 BCrypt）</li>
     *   <li>记录登录日志和失败次数</li>
     * </ul>
     * </p>
     *
     * @param token 认证令牌（包含用户名和密码）
     * @return AuthenticationInfo 认证信息
     * @throws AuthenticationException 认证失败时抛出异常
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) 
            throws AuthenticationException {
        UsernamePasswordToken usernamePasswordToken = (UsernamePasswordToken) token;
        String username = usernamePasswordToken.getUsername();
        logger.debug("执行认证逻辑，用户名: {}", username);

        // 硬编码用户数据（生产环境应从数据库查询）
        String password = null;
        if ("admin".equals(username)) {
            password = "admin123";
        } else if ("user".equals(username)) {
            password = "user123";
        }

        // 用户不存在
        if (password == null) {
            logger.warn("认证失败，用户不存在: {}", username);
            throw new AuthenticationException("用户名或密码错误");
        }

        // 返回认证信息（Shiro 会自动比对密码）
        logger.info("认证成功，用户: {}", username);
        return new SimpleAuthenticationInfo(username, password, getName());
    }
}
