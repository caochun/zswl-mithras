package cn.zswltech.mithras.others.poc;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.StrUtil;
import cn.hutool.setting.yaml.YamlUtil;
import lombok.SneakyThrows;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.EnvironmentStringPBEConfig;
import org.jasypt.salt.NoOpIVGenerator;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;

/**
 * @author luyi
 */
public class PocMithras数据库初始化 {


    //需要导出全量数据的表
    List<String> allDataTable = ListUtil.toList(
            "account_balance_category_dictionary", "address_dictionary", "base_data_special_date",
            "bifrost_custom_tree", "bifrost_custom_tree_menu_ref", "bifrost_role", "bifrost_org", "bifrost_org_menu_function",
            "bifrost_role_menu_function", /*"gruul_user_org_job", "gruul_user_org_role",*/
            "bifrost_function", "bifrost_menu", "bifrost_system_config", "general_dictionary", "industry_type", "system_switch"
    );
    //需要导出部分数据的表
    List<String> partDataTable = ListUtil.toList(
            "bifrost_user --where=account='admin'", "bifrost_user --where=account='zhoufei'",
            "gruul_user_org_job --where=user_id=3", "gruul_user_org_job --where=user_id=106",
            "gruul_user_org_role --where=user_id=3", "gruul_user_org_job --where=user_id=106"
    );

    List<String> extraSqlList = ListUtil.of(
            /*更新密码为1*/
            "update bifrost_user set pwd='2869EB2DCA21656930F97B283FE68E48',salt='5a042f25f3b84afbac633cfa7ab68054' where account='admin';" + System.lineSeparator(),
            /**/
            ""
    );

    String DUMP_DATA_SQL = "/usr/local/mysql-5.7.31-macos10.14-x86_64/bin/mysqldump " +
            "mithras %s " +
            "--skip-disable-keys --skip-add-locks --skip-lock-tables  --create-options " +
            "--add-drop-table --extended-insert -t --user=%s --password=%s --host=%s --port=3306";


    @SneakyThrows
    public static void main(String[] args) {
        PocMithras数据库初始化 poc = new PocMithras数据库初始化();
        poc.generateInitSql();

    }

    @SneakyThrows
    public void generateInitSql() {
        StringBuilder builder = new StringBuilder();
        DataSource ds = dbDataSource();
        Connection conn = ds.getConnection();
        builder.append(表格DDL(conn));
        builder.append(表格数据(conn));
        extraSqlList.forEach(builder::append);
        writeToFile(builder);
    }

    private void writeToFile(StringBuilder builder) {
        Path path = Paths.get(System.getProperty("user.home"), "Desktop", "mithras_init.sql");
        FileUtil.writeUtf8String(builder.toString(), path.toFile());
    }


    @SneakyThrows
    public String 表格数据(Connection conn) {
        StringBuilder sql = new StringBuilder();
        String url = getConfig("spring.datasource.mithras.url");
        String host = url.substring(url.indexOf("//") + 2, url.lastIndexOf(":"));
        //
        //
        for (String part : partDataTable) {
            String dumpSql = String.format(
                    DUMP_DATA_SQL,
                    part,
                    getConfig("spring.datasource.mithras.username"),
                    getConfig("spring.datasource.mithras.password"),
                    host
            );
            String read = IoUtil.read(new InputStreamReader(Runtime.getRuntime().exec(dumpSql).getInputStream()), true);
            sql.append(read);
        }
        //

        String dumpSql = String.format(
                DUMP_DATA_SQL,
                StrUtil.join(" ", allDataTable),
                getConfig("spring.datasource.mithras.username"),
                getConfig("spring.datasource.mithras.password"),
                host
        );
        Process process = Runtime.getRuntime().exec(dumpSql);
        InputStreamReader inputStreamReader = new InputStreamReader(process.getInputStream(), "utf-8");
        sql.append(IoUtil.read(inputStreamReader, true));

        return sql.toString();
    }

    @SneakyThrows
    private String 表格DDL(Connection conn) {
        StringBuilder result = new StringBuilder();
        try (Statement stat1 = conn.createStatement();
             Statement stat2 = conn.createStatement()) {
            ResultSet rs = stat1.executeQuery("show tables");
            while (rs.next()) {
                String tableName = rs.getString(1);
                if (tableName.startsWith("act_") || tableName.startsWith("flw_")) {
                    //审批流的表跳过；
                    continue;
                }
                ResultSet resultSet = stat2.executeQuery("show create table " + tableName);
                if (resultSet.next()) {
                    String ddl = resultSet.getString(2);
                    result.append(String.format("DROP TABLE IF EXISTS `%s`;", tableName)).append(System.lineSeparator());
                    result.append(ddl).append(";").append(System.lineSeparator());
                }
            }
        }
        return result.toString();
    }

    @SneakyThrows
    public String getConfig(String path) {
        Dict dict = YamlUtil.load(IoUtil.getBomReader(new ClassPathResource("application-dev.yml").getInputStream()));
        Dict dict2 = YamlUtil.load(IoUtil.getBomReader(new ClassPathResource("application.yml").getInputStream()));
        if (path.endsWith("password")) {
            return decrypt(dict.getByPath(path),
                    dict2.getByPath("jasypt.encryptor.password")
            );
        } else {
            return dict.getByPath(path);
        }
    }


    @SneakyThrows
    public DataSource dbDataSource() {
        Dict dict = YamlUtil.load(IoUtil.getBomReader(new ClassPathResource("application-dev.yml").getInputStream()));
        Dict dict2 = YamlUtil.load(IoUtil.getBomReader(new ClassPathResource("application.yml").getInputStream()));
        DriverManagerDataSource driverManagerDataSource = new DriverManagerDataSource();
        driverManagerDataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        driverManagerDataSource.setUsername(dict.getByPath("spring.datasource.mithras.username"));
        driverManagerDataSource.setPassword(
                decrypt(dict.getByPath("spring.datasource.mithras.password"),
                        dict2.getByPath("jasypt.encryptor.password")
                )
        );
        driverManagerDataSource.setUrl(dict.getByPath("spring.datasource.mithras.url"));
        return driverManagerDataSource;
    }

    public static String decrypt(String content, String pwd) {
        if (content.startsWith("ENC(")) {
            content = content.substring(content.indexOf("(") + 1, content.indexOf(")"));
            StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
            EnvironmentStringPBEConfig config = new EnvironmentStringPBEConfig();
            config.setAlgorithm("PBEWithMD5AndDES");
            config.setPassword(pwd);
            config.setIvGenerator(new NoOpIVGenerator());
            encryptor.setConfig(config);
            return encryptor.decrypt(content);
        } else {
            return content;
        }
    }
}
