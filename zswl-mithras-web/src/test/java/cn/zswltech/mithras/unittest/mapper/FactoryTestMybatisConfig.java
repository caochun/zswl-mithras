package cn.zswltech.mithras.unittest.mapper;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.lang.Dict;
import cn.hutool.setting.yaml.YamlUtil;
import com.baomidou.mybatisplus.core.MybatisConfiguration;
import lombok.SneakyThrows;
import org.apache.ibatis.logging.stdout.StdOutImpl;
import org.apache.ibatis.session.SqlSessionFactory;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.EnvironmentStringPBEConfig;
import org.jasypt.salt.NoOpIVGenerator;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.support.TransactionTemplate;

import javax.sql.DataSource;

/**
 * @author luyi
 */
@EnableTransactionManagement
@MapperScan(basePackages = "cn.zswltech.mithras.factory.mapper")
public class FactoryTestMybatisConfig {

    @Bean(name = "testDataSource")
    @SneakyThrows
    public DataSource dbDataSource() {
        Dict dict = YamlUtil.load(IoUtil.getBomReader(new ClassPathResource("application-dev.yml").getInputStream()));
        Dict dict2 = YamlUtil.load(IoUtil.getBomReader(new ClassPathResource("application.yml").getInputStream()));
        DriverManagerDataSource driverManagerDataSource = new DriverManagerDataSource();
        driverManagerDataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        driverManagerDataSource.setUsername(dict.getByPath("spring.datasource.factory.username"));
        driverManagerDataSource.setPassword(
                decrypt(dict.getByPath("spring.datasource.factory.password"),
                        dict2.getByPath("jasypt.encryptor.password")
                )
        );
        driverManagerDataSource.setUrl(dict.getByPath("spring.datasource.factory.url"));
        return driverManagerDataSource;


    }

    @Bean(name = "testSqlSessionFactory")
    public SqlSessionFactory dbSqlSessionFactory(@Qualifier("testDataSource") DataSource dataSource,
                                                 @Value("classpath*:mapper/*Mapper.xml") Resource[] mapperLocations) throws Exception {
        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
        bean.setDataSource(dataSource);
        bean.setMapperLocations(mapperLocations);
        MybatisConfiguration configuration = new MybatisConfiguration();
        configuration.setMapUnderscoreToCamelCase(true);
        // 配置打印sql语句
        configuration.setLogImpl(StdOutImpl.class);
        bean.setConfiguration(configuration);
        return bean.getObject();
    }

    @Bean(name = "testTransactionManager")
    public DataSourceTransactionManager dbTransactionManager(@Qualifier("testDataSource") DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean("transactionTemplate")
    public TransactionTemplate transactionTemplate(@Qualifier("testTransactionManager") DataSourceTransactionManager transactionManager) {
        return new TransactionTemplate(transactionManager);
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
