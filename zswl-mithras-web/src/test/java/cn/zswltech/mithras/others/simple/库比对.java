package cn.zswltech.mithras.others.simple;

import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.ObjectUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLSyntaxErrorException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
/**
 * @author luyi
 */
public class 库比对 {

    public static void main(String[] args) {
        库比对 kbd = new 库比对();
        //新机房
        DriverManagerDataSource ds1 = new DriverManagerDataSource();
        ds1.setDriverClassName("com.mysql.cj.jdbc.Driver");
        ds1.setUsername("zswldba");
        ds1.setPassword("Zszl#%2309");
        ds1.setUrl("jdbc:mysql://10.158.32.186:3306/mithras?useUnicode=true&characterEncoding=UTF8&zeroDateTimeBehavior=convertToNull&useSSL=false&serverTimezone=Asia/Shanghai&tinyInt1isBit=false&nullCatalogMeansCurrent=true&rewriteBatchedStatements=true");
        //老机房
        DriverManagerDataSource ds2 = new DriverManagerDataSource();
        ds2.setDriverClassName("com.mysql.cj.jdbc.Driver");
        ds2.setUsername("zswldba");
        ds2.setPassword("Zszl#%2309");
        ds2.setUrl("jdbc:mysql://10.158.20.144:3306/mithras?useUnicode=true&characterEncoding=UTF8&zeroDateTimeBehavior=convertToNull&useSSL=false&serverTimezone=Asia/Shanghai&tinyInt1isBit=false&nullCatalogMeansCurrent=true&rewriteBatchedStatements=true");
        kbd.compare(ds1, ds2);

    }

    @SneakyThrows
    public void compare(DataSource ds1, DataSource ds2) {
        Connection conn2 = ds2.getConnection();
        Connection conn1 = ds1.getConnection();
        try (
                Statement stat1 = conn1.createStatement();
                Statement stat2 = conn2.createStatement()) {
            ResultSet rs = stat2.executeQuery("show tables");
            while (rs.next()) {
                ThreadUtil.sleep(500);
                String tableName = rs.getString(1);
                if (tableName.startsWith("lifecycle")) {
                    continue;
                }
                compareFields(conn1.createStatement(), conn2.createStatement(), tableName);
                compareRows(conn1.createStatement(), conn2.createStatement(), tableName);
            }
        }
    }

    @SneakyThrows
    private void compareRows(Statement stat1, Statement stat2, String tableName) {
        ResultSet rs2 = stat2.executeQuery("select count(1) from " + tableName);
        if (rs2.next()) {
            long rows2 = rs2.getLong(1);
            try {
                ResultSet rs1 = stat1.executeQuery("select count(1) from " + tableName);
                if (rs1.next()) {
                    long rows1 = rs1.getLong(1);
                    if (rows2 != rows1) {
                        log.error(String.format("行数不一样，新库：%s，老库：%s：%s", rows1, rows2, tableName));
                    }
                }
            } catch (SQLSyntaxErrorException e) {
                if (e.getMessage().contains("doesn't exist")) {
                    log.error("新机房预发库不存在表：" + tableName);
                    return;
                }
            }
        }
        //最新一行数据比对
        try {
            String orderSql = tableName.startsWith("act_") || tableName.startsWith("flw_") ? "order by ID_ DESC" : "order by id DESC";
            rs2 = stat2.executeQuery("select * from " + tableName + " " + orderSql + " limit 1 ");
            if (rs2.next()) {
                Object obj2 = rs2.getObject(1);
                ResultSet rs1 = stat1.executeQuery("select * from " + tableName + " " + orderSql + " limit 1 ");
                if (rs1.next()) {
                    Object obj1 = rs1.getObject(1);
                    if (!ObjectUtil.equal(obj1, obj2)) {
                        log.error(String.format("最新一条数据ID不一样，新库：%s，老库：%s：%s", obj1, obj2, tableName));
                    }
                }
            }
        } catch (SQLSyntaxErrorException ee) {
            if (!ee.getMessage().contains("Unknown column 'ID_' in 'order clause'")
                    && !ee.getMessage().contains("Unknown column 'id' in 'order clause'")
            ) {
                throw new Exception(ee);
            } else {
                log.error("表没有id。" + tableName);
            }
        } catch (Exception e) {
            log.error("最新一行提取错误。" + tableName, e);
        }

    }

    @SneakyThrows
    private void compareFields(Statement stat1, Statement stat2, String tableName) {
        ResultSet rs2 = stat2.executeQuery("show create table " + tableName);
        if (rs2.next()) {
            String ddl2 = rs2.getString(2);
            Set<String> fields2 = extractFields(ddl2);
            try {
                ResultSet rs1 = stat1.executeQuery("show create table " + tableName);
                if (rs1.next()) {
                    String ddl1 = rs1.getString(2);
                    Set<String> fields1 = extractFields(ddl1);
                    Set<String> set = new HashSet<>(fields1);
                    set.addAll(fields2);
                    for (String key : set) {
                        if (!(fields1.contains(key) && fields2.contains(key))) {
                            log.error("字段不一致：" + tableName);
                        }
                    }
                }
            } catch (SQLSyntaxErrorException e) {
                if (e.getMessage().contains("doesn't exist")) {
                    log.error("新机房预发库不存在表：" + tableName);
                }
            }

        }

    }

    private Set<String> extractFields(String ddl) {
        String[] parts = ddl.split("\\n");
        return Arrays.stream(parts).filter(e -> e.trim().startsWith("`")).map(e -> {
            String[] split = e.split("`");
            return split[0] + split[1];
        }).sorted().collect(Collectors.toSet());
    }

}
