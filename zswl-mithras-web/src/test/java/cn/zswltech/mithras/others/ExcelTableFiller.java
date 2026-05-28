package cn.zswltech.mithras.others;

import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Paths;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static cn.hutool.db.DbUtil.close;

/**
 * @author junke
 */
public class ExcelTableFiller {
    private static final String driverName = "com.mysql.cj.jdbc.Driver";       // 数据库驱动名称

    private static final String url = "jdbc:mysql://weilai-dev2:3306/mithras?useUnicode=true&characterEncoding=UTF8&zeroDateTimeBehavior=convertToNull&useSSL=false&serverTimezone=Asia/Shanghai&tinyInt1isBit=false&nullCatalogMeansCurrent=true";    // 数据库url

    private static final String userName = "tongdun";          // 用户名

    private static final String password = "Td@1010";     // 用户密码

    private static Connection conn = null;                  // 数据库连接对象

    private static PreparedStatement preparedStatement = null;  // 执行操作对象

    static {

        try {
            Class.forName(driverName);      // 2、注册数据库驱动
            conn = DriverManager.getConnection(url, userName, password);    // 3、获取数据库连接
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * 查询数据
     *
     * @return 返回查询到结果
     */
    public static Map<String, String> queryTables() {
        Map<String, String> map = new TreeMap<>();
        ResultSet result = null;
        try {
            String sql = "select TABLE_NAME, TABLE_COMMENT from information_schema.TABLES where  TABLE_TYPE='BASE TABLE' and TABLE_SCHEMA='mithras' order by TABLE_NAME";
            preparedStatement = conn.prepareStatement(sql);         // 4、执行预编译SQL操作
            result = preparedStatement.executeQuery();  // 5、执行查询操作
            while (result.next()) {
                String name = result.getString(1);
                if (name.startsWith("act_")
                        || name.startsWith("flw_")
                        || name.equals("future_log")) {
                    continue;
                }
                map.put(result.getString(1), result.getString(2));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // 7、释放资源
            close(conn, preparedStatement, result);
        }
        return map;
    }

    @SneakyThrows
    @Test
    public void fill() {
        Map<String, String> tableMap = queryTables();
        List<List<String>> data = new ArrayList<>();
        for (String name : tableMap.keySet()) {
            List<String> rowData = new ArrayList<>();
            rowData.add("");
            rowData.add(name);
            rowData.add(tableMap.get(name));
            rowData.add("浙商租赁核心业务系统");
            rowData.add("");
            rowData.add("Mysql增量+存量");
            rowData.add("");
            rowData.add("");
            rowData.add("");
            rowData.add("mysql");
            rowData.add("5.7.25");
            rowData.add("id");
            rowData.add("时间字段");
            rowData.add("update_time");
            rowData.add("按天更新");
            data.add(rowData);
        }
        File file = Paths.get("/Users/luyi/Downloads/文件/租赁/集团对接", "数据调研表2.xlsx").toFile();
        try (ExcelWriter writer = ExcelUtil.getWriter(file, "总目录")) {
            writer.passRows(6);
            writer.write(data, true);
        }
    }
}
