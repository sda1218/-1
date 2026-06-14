/**
 * Security 层 - 安全认证和授权
 * 
 * <p>职责：
 * <ul>
 *   <li>用户身份认证（Authentication）</li>
 *   <li>用户权限授权（Authorization）</li>
 *   <li>自定义 Shiro Realm 实现</li>
 *   <li>安全相关工具类</li>
 * </ul>
 * 
 * <p>约束：
 * <ul>
 *   <li>Realm 类继承 AuthorizingRealm</li>
 *   <li>使用 @Component 注解注册为 Spring Bean</li>
 *   <li>生产环境必须使用加密算法存储密码</li>
 *   <li>避免在日志中输出敏感信息（密码、Token）</li>
 * </ul>
 * 
 * @author Drone Management Team
 * @version 1.0.0
 * @since 2024-01-15
 */
package com.example.drone.security;
