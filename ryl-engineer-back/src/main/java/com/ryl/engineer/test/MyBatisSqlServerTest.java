package com.ryl.engineer.test;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * MyBatis与SQL Server集成测试类
 * 使用原生MyBatis API直接连接数据库进行测试
 */
public class MyBatisSqlServerTest {

    public static void main(String[] args) {
        SqlSession sqlSession = null;
        
        try {
            System.out.println("=== MyBatis SQL Server 集成测试 ===");
            
            // 1. 加载MyBatis配置文件
            String resource = "mybatis-config-test.xml";
            InputStream inputStream = Resources.getResourceAsStream(resource);
            
            // 2. 创建SqlSessionFactory
            SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
            
            // 3. 获取SqlSession
            sqlSession = sqlSessionFactory.openSession();
            
            // 4. 测试查询用户
            System.out.println("\n测试查询用户表：");
            List<Map<String, Object>> users = sqlSession.selectList("com.ryl.engineer.mapper.UserMapper.findAllUsers");
            
            if (users != null && !users.isEmpty()) {
                System.out.println("用户表查询结果：");
                System.out.println("----------------------------------");
                System.out.println("ID\t用户名\t\t姓名\t\t角色");
                System.out.println("----------------------------------");
                
                for (Map<String, Object> user : users) {
                    System.out.println(user.get("id") + "\t" + 
                                      user.get("username") + "\t\t" + 
                                      user.get("realName") + "\t\t" + 
                                      user.get("role"));
                }
            } else {
                System.out.println("未查询到用户数据");
            }
            
            // 5. 测试查询任务
            System.out.println("\n测试查询任务表：");
            List<Map<String, Object>> tasks = sqlSession.selectList("com.ryl.engineer.mapper.TaskMapper.findAllTasks");
            
            if (tasks != null && !tasks.isEmpty()) {
                System.out.println("任务表查询结果：");
                System.out.println("------------------------------------------");
                System.out.println("任务ID\t\t标题\t\t类型\t\t状态");
                System.out.println("------------------------------------------");
                
                for (Map<String, Object> task : tasks) {
                    System.out.println(task.get("taskId") + "\t" + 
                                      task.get("title") + "\t\t" + 
                                      task.get("taskType") + "\t\t" + 
                                      task.get("status"));
                }
            } else {
                System.out.println("未查询到任务数据");
            }
            
            // 6. 测试根据ID查询用户
            System.out.println("\n测试根据ID查询用户：");
            Map<String, Object> params = new HashMap<>();
            params.put("id", 1);
            Map<String, Object> user = sqlSession.selectOne("com.ryl.engineer.mapper.UserMapper.findUserById", params);
            
            if (user != null) {
                System.out.println("用户ID: " + user.get("id"));
                System.out.println("用户名: " + user.get("username"));
                System.out.println("真实姓名: " + user.get("realName"));
                System.out.println("角色: " + user.get("role"));
            } else {
                System.out.println("未找到ID为1的用户");
            }
            
        } catch (IOException e) {
            System.err.println("加载MyBatis配置文件失败: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("MyBatis操作失败: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // 7. 关闭SqlSession
            if (sqlSession != null) {
                sqlSession.close();
                System.out.println("\nSqlSession已关闭");
            }
        }
    }
} 