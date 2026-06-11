package cn.zswltech.mithras.creditreport.config;

import cn.zswltech.mithras.foundation.persistence.plugin.CustomSqlInjector;
import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceBuilder;
import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.MybatisXMLLanguageDriver;
import com.baomidou.mybatisplus.core.config.GlobalConfig;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.logging.slf4j.Slf4jImpl;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.type.JdbcType;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Configuration
@Slf4j
@MapperScan(value = "cn.zswltech.mithras.creditreport.mapper", sqlSessionFactoryRef = "creditReportSqlSessionFactory")
public class CreditReportMysqlConfig {

    @Autowired
    private CustomSqlInjector customSqlInjector;

    @Value("${spring.profiles.active}")
    private String active;

    private static final String PRE = "pre";

    private static final String PROD = "prod";

    @Bean("creditReportDataSource")
    @ConfigurationProperties("spring.datasource.report")
    public DataSource creditReportDataSource(){
        return DruidDataSourceBuilder.create().build();
    }

    @Bean("creditReportTransactionManager")
    public DataSourceTransactionManager creditReportTransactionManager() {
        return new DataSourceTransactionManager(creditReportDataSource());
    }

    @Bean("creditReportGlobalConfiguration")
    public GlobalConfig creditReportGlobalConfiguration() {
        GlobalConfig conf = new GlobalConfig();
        GlobalConfig.DbConfig dbconf = new GlobalConfig.DbConfig();
        dbconf.setLogicDeleteValue("1");
        dbconf.setLogicNotDeleteValue("0");
        dbconf.setLogicDeleteField("deleted");
        conf.setDbConfig(dbconf);
        conf.setSqlInjector(customSqlInjector);
        return conf;
    }

    /**
     * 设置属性
     */
    @Bean("creditReportSqlSessionFactory")
    public SqlSessionFactory creditReportSqlSessionFactory(ObjectProvider<Interceptor[]> interceptorsProvider) throws Exception {
        MybatisSqlSessionFactoryBean sqlSessionFactory = new MybatisSqlSessionFactoryBean();
        sqlSessionFactory.setDataSource(creditReportDataSource());
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        MybatisConfiguration configuration = new MybatisConfiguration();
        configuration.setDefaultScriptingLanguage(MybatisXMLLanguageDriver.class);
        configuration.setJdbcTypeForNull(JdbcType.NULL);
        configuration.setMapUnderscoreToCamelCase(true); //驼峰标识
        configuration.setCacheEnabled(false);
        configuration.setCallSettersOnNulls(true);
        //预发生产
        configuration.setLogImpl(Slf4jImpl.class);
//        if(PRE.equals(active) || PROD.equals(active)){
//            configuration.setLogImpl(Slf4jImpl.class);
//        }else {
//            configuration.setLogImpl(StdOutImpl.class);
//        }
        sqlSessionFactory.setConfiguration(configuration);
//        sqlSessionFactory.setPlugins(new Interceptor[]{
////    		performanceInterceptor(), //性能分析
//                // optimisticLockerInterceptor(),//乐观锁
//                paginationInterceptor(),//添加分页功能
//                auditDataInterceptor() // 自动审核
//
//        });
        // 自动寻找所有spring bean内的plugin
        sqlSessionFactory.setPlugins(interceptorsProvider.getIfAvailable());
        sqlSessionFactory.setGlobalConfig(creditReportGlobalConfiguration());
        sqlSessionFactory.setMapperLocations(resolveMapperLocations()); // Mapper包路径
        return sqlSessionFactory.getObject();
    }

    public Resource[] resolveMapperLocations() {
        ResourcePatternResolver resourceResolver = new PathMatchingResourcePatternResolver();
        List<String> mapperLocations = new ArrayList<>();
        mapperLocations.add("classpath*:creditreport/mapper/*.xml");
        List<Resource> resources = new ArrayList();
        if (!CollectionUtils.isEmpty(mapperLocations)) {
            for (String mapperLocation : mapperLocations) {
                try {
                    Resource[] mappers = resourceResolver.getResources(mapperLocation);
                    resources.addAll(Arrays.asList(mappers));
                } catch (IOException e) {
                    log.error("Get myBatis resources happened exception", e);
                }
            }
        }

        return resources.toArray(new Resource[resources.size()]);
    }


}


