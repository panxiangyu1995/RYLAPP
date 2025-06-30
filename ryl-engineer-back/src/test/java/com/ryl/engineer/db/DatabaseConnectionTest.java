package com.ryl.engineer.db;

import com.ryl.engineer.entity.User;
import com.ryl.engineer.mapper.UserMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 数据库连接和CRUD操作测试
 */
@SpringBootTest
public class DatabaseConnectionTest {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private UserMapper userMapper;

    /**
     * 测试数据库连接是否正常
     */
    @Test
    public void testDatabaseConnection() throws SQLException {
        assertNotNull(dataSource, "DataSource应该被注入");
        
        Connection connection = dataSource.getConnection();
        assertNotNull(connection, "应该能够获取数据库连接");
        
        // 检查连接是否有效
        assertTrue(connection.isValid(1), "数据库连接应该有效");
        
        // 获取数据库信息
        String databaseProductName = connection.getMetaData().getDatabaseProductName();
        String databaseProductVersion = connection.getMetaData().getDatabaseProductVersion();
        
        System.out.println("数据库类型: " + databaseProductName);
        System.out.println("数据库版本: " + databaseProductVersion);
        
        // 关闭连接
        connection.close();
        System.out.println("数据库连接测试通过");
    }

    /**
     * 测试直接使用JDBC执行SQL查询
     */
    @Test
    public void testJdbcQuery() {
        assertNotNull(jdbcTemplate, "JdbcTemplate应该被注入");
        
        // 查询用户表
        String sql = "SELECT COUNT(*) FROM user";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class);
        
        assertNotNull(count, "查询结果不应为空");
        System.out.println("用户表中的记录数: " + count);
        
        // 查询角色表
        sql = "SELECT * FROM role LIMIT 3";
        List<Map<String, Object>> roles = jdbcTemplate.queryForList(sql);
        
        assertFalse(roles.isEmpty(), "角色表应该有数据");
        System.out.println("角色表中的前3条记录: ");
        roles.forEach(role -> {
            System.out.println("ID: " + role.get("id") + ", 名称: " + role.get("name") + ", 编码: " + role.get("code"));
        });
    }

    /**
     * 测试使用MyBatis Mapper进行CRUD操作
     */
    @Test
    @Transactional // 使用事务，测试完成后会自动回滚，不会影响数据库
    public void testUserMapperCrud() {
        assertNotNull(userMapper, "UserMapper应该被注入");
        
        // 1. 查询测试
        User adminUser = userMapper.selectByWorkId("admin");
        assertNotNull(adminUser, "应该能够查询到admin用户");
        System.out.println("查询到admin用户: " + adminUser.getName());
        
        // 2. 插入测试
        User newUser = new User();
        newUser.setWorkId("TEST001");
        newUser.setName("测试用户");
        newUser.setMobile("13888888888");
        newUser.setPassword("$2a$10$3YGMqcQHDv.rYFvgEYhx8.z.y1KsS3SOz6GJI4tUgK9n69e.2zKI2"); // 加密后的123456
        newUser.setDepartment("测试部");
        newUser.setLocation("测试地点");
        newUser.setStatus(1);
        newUser.setCreateTime(new Date());
        newUser.setUpdateTime(new Date());
        
        int insertResult = userMapper.insert(newUser);
        assertEquals(1, insertResult, "插入操作应该成功");
        assertNotNull(newUser.getId(), "插入后应该获得ID");
        System.out.println("插入测试用户成功，ID: " + newUser.getId());
        
        // 3. 查询插入的用户
        User insertedUser = userMapper.selectByWorkId("TEST001");
        assertNotNull(insertedUser, "应该能够查询到新插入的用户");
        assertEquals("测试用户", insertedUser.getName(), "用户名应该匹配");
        
        // 4. 更新测试
        insertedUser.setName("更新后的测试用户");
        insertedUser.setDepartment("更新后的部门");
        int updateResult = userMapper.update(insertedUser);
        assertEquals(1, updateResult, "更新操作应该成功");
        
        // 5. 验证更新
        User updatedUser = userMapper.selectById(insertedUser.getId());
        assertEquals("更新后的测试用户", updatedUser.getName(), "更新后的名称应该匹配");
        assertEquals("更新后的部门", updatedUser.getDepartment(), "更新后的部门应该匹配");
        
        System.out.println("用户CRUD测试通过");
    }
} 