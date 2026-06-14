package com.example.drone.config;

import com.example.drone.security.DroneRealm;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * ShiroConfig 配置类单元测试.
 * <p>
 * 测试 Apache Shiro 配置是否正确，包括：
 * <ul>
 *   <li>SecurityManager 配置</li>
 *   <li>ShiroFilterFactoryBean 配置</li>
 *   <li>过滤器链规则配置</li>
 * </ul>
 * </p>
 *
 * @author Drone Management System
 * @version 1.0.0
 * @since 2024-01-15
 */
@SpringBootTest
class ShiroConfigTest {

    @Autowired
    private SecurityManager securityManager;

    @Autowired
    private ShiroFilterFactoryBean shiroFilterFactoryBean;

    @Autowired
    private DroneRealm droneRealm;

    /**
     * 测试 SecurityManager Bean 是否正确配置.
     */
    @Test
    void testSecurityManagerConfiguration() {
        // 验证 SecurityManager 不为空
        assertNotNull(securityManager, "SecurityManager 应该被正确配置");

        // 验证 SecurityManager 类型
        assertTrue(securityManager instanceof DefaultWebSecurityManager,
                "SecurityManager 应该是 DefaultWebSecurityManager 类型");

        // 验证 Realm 已设置
        DefaultWebSecurityManager webSecurityManager = (DefaultWebSecurityManager) securityManager;
        assertNotNull(webSecurityManager.getRealms(), "SecurityManager 应该配置了 Realm");
        assertEquals(1, webSecurityManager.getRealms().size(), "应该只有一个 Realm");
    }

    /**
     * 测试 DroneRealm Bean 是否正确配置.
     */
    @Test
    void testDroneRealmConfiguration() {
        // 验证 DroneRealm 不为空
        assertNotNull(droneRealm, "DroneRealm 应该被正确配置");

        // 验证 Realm 名称
        assertNotNull(droneRealm.getName(), "DroneRealm 应该有名称");
    }

    /**
     * 测试 ShiroFilterFactoryBean 是否正确配置.
     */
    @Test
    void testShiroFilterFactoryBeanConfiguration() {
        // 验证 ShiroFilterFactoryBean 不为空
        assertNotNull(shiroFilterFactoryBean, "ShiroFilterFactoryBean 应该被正确配置");

        // 验证 SecurityManager 已设置
        assertNotNull(shiroFilterFactoryBean.getSecurityManager(),
                "ShiroFilterFactoryBean 应该配置了 SecurityManager");
    }

    /**
     * 测试过滤器链规则是否正确配置.
     * <p>
     * 验证规则：
     * <ul>
     *   <li>/** 匿名访问（anon）</li>
     * </ul>
     * </p>
     */
    @Test
    void testFilterChainDefinition() {
        // 获取过滤器链定义
        Map<String, String> filterChainDefinitionMap = 
                shiroFilterFactoryBean.getFilterChainDefinitionMap();

        // 验证过滤器链不为空
        assertNotNull(filterChainDefinitionMap, "过滤器链定义不应该为空");

        // 验证规则数量
        assertEquals(1, filterChainDefinitionMap.size(),
                "应该有 1 条过滤器链规则");

        // 验证 /** 匿名访问
        assertEquals("anon", filterChainDefinitionMap.get("/**"),
                "/** 应该允许匿名访问（anon）");
    }
}
