package cn.zswltech.mithras.others.generator.util;

import cn.zswltech.mithras.others.generator.GeneratorMain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 从数据库获取建表语句
 *
 * @author wangchuanhao
 * @date 2022/7/17 11:53 PM
 */
public class TableUtil {

    private static Logger log = LoggerFactory.getLogger(TableUtil.class);

    public static String getDDLByTableName(String tableName) {
        Connection conn = getConnection();
        String sql = String.format("SHOW CREATE TABLE %s", tableName);
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(sql);
            //ps.setString(1, tableName);
            ResultSet resultSet = ps.executeQuery();
            while (resultSet.next()) {
                //System.out.println(resultSet.getString(1));//第一个参数获取的是tableName
                //System.out.println(resultSet.getString(2));//第二个参数获取的是表的ddl语句
                return resultSet.getString(2);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if(null != ps){
                try {
                    ps.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if(null != conn) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    public static Connection getConnection() {
        Connection connection = null;
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(GeneratorMain.MYSQL_URL, GeneratorMain.MYSQL_USER, GeneratorMain.MYSQL_PWD);
        } catch (ClassNotFoundException | SQLException e) {
            log.error(GeneratorMain.MYSQL_URL + "连接MySQL数据库失败！", e);
            return null;
        }
        return connection;
    }

}
