package cn.zswltech.mithras.others.poc;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.lang.Dict;
import cn.hutool.setting.yaml.YamlUtil;
import lombok.SneakyThrows;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.EnvironmentStringPBEConfig;
import org.jasypt.salt.NoOpIVGenerator;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * @author luyi
 */
public class PocReport数据库初始化 {
    @SneakyThrows
    public static void main(String[] args) {
        PocReport数据库初始化 poc = new PocReport数据库初始化();
        poc.generateInitSql();

    }

    @SneakyThrows
    public void generateInitSql() {
        StringBuilder builder = new StringBuilder();
        DataSource ds = dbDataSource();
        Connection conn = ds.getConnection();
        builder.append(表格DDL(conn));
        writeToFile(builder);
    }

    private void writeToFile(StringBuilder builder) {
        Path path = Paths.get(System.getProperty("user.home"), "Desktop", "report_init.sql");
        FileUtil.writeUtf8String(builder.toString(), path.toFile());
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
    public DataSource dbDataSource() {
        Dict dict = YamlUtil.load(IoUtil.getBomReader(new ClassPathResource("application-dev.yml").getInputStream()));
        Dict dict2 = YamlUtil.load(IoUtil.getBomReader(new ClassPathResource("application.yml").getInputStream()));
        DriverManagerDataSource driverManagerDataSource = new DriverManagerDataSource();
        driverManagerDataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        driverManagerDataSource.setUsername(dict.getByPath("spring.datasource.report.username"));
        driverManagerDataSource.setPassword(
                decrypt(dict.getByPath("spring.datasource.report.password"),
                        dict2.getByPath("jasypt.encryptor.password")
                )
        );
        driverManagerDataSource.setUrl(dict.getByPath("spring.datasource.report.url"));
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
