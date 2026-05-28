package cn.zswltech.mithras.others.function;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.sql.*;
import java.util.HashSet;
import java.util.Set;

/**
 * @author yibin
 */
public class FunctionCheck {

    // MySQL 8.0 以下版本 - JDBC 驱动名及数据库 URL
    static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    static final String DB_URL_1 = "jdbc:mysql://weilai-dev2:3306/mithras";
    static final String USER_1 = "tongdun";
    static final String PASS_1 = "Td@1010";

    static final String DB_URL_2 = "jdbc:mysql://10.158.20.144:3306/mithras";
    static final String USER_2 = "zszl";
    static final String PASS_2 = "123456";


    @Test
    public void check() {
        Set<String> set1 = getList(DB_URL_1, USER_1, PASS_1);
        Set<String> set2 = getList(DB_URL_2, USER_2, PASS_2);

        set1.removeAll(set2);
        System.out.println(set1);
        Assertions.assertTrue(set1.isEmpty());

    }

    public Set<String> getList(String DB_URL, String USER, String PASS) {
        Set<String> codeSet = new HashSet<>();
        Connection conn = null;
        Statement stmt = null;
        try {
            // 注册 JDBC 驱动
            Class.forName(JDBC_DRIVER);
            // 打开链接
            System.out.println("连接数据库...");
            conn = DriverManager.getConnection(DB_URL, USER, PASS);

            // 执行查询
            System.out.println(" 实例化Statement对象...");
            stmt = conn.createStatement();
            String sql;
            sql = "SELECT code FROM bifrost_function";
            ResultSet rs = stmt.executeQuery(sql);

            // 展开结果集数据库
            while (rs.next()) {
                // 通过字段检索
                codeSet.add(rs.getString("code"));
            }
            // 完成后关闭
            rs.close();
            stmt.close();
            conn.close();
        } catch (SQLException se) {
            // 处理 JDBC 错误
            se.printStackTrace();
        } catch (Exception e) {
            // 处理 Class.forName 错误
            e.printStackTrace();
        } finally {
            // 关闭资源
            try {
                if (stmt != null) {
                    stmt.close();
                }
            } catch (SQLException se2) {
            }// 什么都不做
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
        return codeSet;
    }
}
