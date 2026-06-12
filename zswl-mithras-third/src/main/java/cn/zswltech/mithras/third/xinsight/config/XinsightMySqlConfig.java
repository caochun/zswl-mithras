package cn.zswltech.mithras.third.xinsight.config;

import cn.zswltech.mithras.foundation.persistence.plugin.CustomSqlInjector;
import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceBuilder;
import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.MybatisXMLLanguageDriver;
import com.baomidou.mybatisplus.core.config.GlobalConfig;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.logging.slf4j.Slf4jImpl;
import org.apache.ibatis.logging.stdout.StdOutImpl;
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

@Slf4j
@Data
@Configuration
@MapperScan(basePackages = "cn.zswltech.mithras.third.xinsight.persistence.mapper", sqlSessionFactoryRef = "xinsightSqlSessionFactory")
public class XinsightMySqlConfig {

    @Autowired
    private CustomSqlInjector customSqlInjector;

    @Value("${spring.profiles.active}")
    private String active;

    private static final String PRE = "pre";

    private static final String PROD = "prod";

    /**
     * 慧眼系统数据源
     */
    @Bean("xinsightDataSource")
    @ConfigurationProperties("spring.datasource.xinsight")
    public DataSource xinsightDataSource() {
        return DruidDataSourceBuilder.create().build();
    }

    /**
     * 慧眼系统事务管理器
     */
    @Bean("xinsightTransactionManager")
    public DataSourceTransactionManager xinsightTransactionManager() {
        return new DataSourceTransactionManager(xinsightDataSource());
    }

    @Bean("xinsightGlobalConfiguration")
    public GlobalConfig xinsightGlobalConfiguration() {
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
     * 慧眼系统SqlSessionFactory
     */
    @Bean("xinsightSqlSessionFactory")
    public SqlSessionFactory xinsightSqlSessionFactory(ObjectProvider<Interceptor[]> interceptorsProvider) throws Exception {
        MybatisSqlSessionFactoryBean sqlSessionFactory = new MybatisSqlSessionFactoryBean();
        sqlSessionFactory.setDataSource(xinsightDataSource());
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        MybatisConfiguration configuration = new MybatisConfiguration();
        configuration.setDefaultScriptingLanguage(MybatisXMLLanguageDriver.class);
        configuration.setJdbcTypeForNull(JdbcType.NULL);
        configuration.setMapUnderscoreToCamelCase(true); //驼峰标识
        configuration.setCacheEnabled(false);
        configuration.setCallSettersOnNulls(true);
        //预发生产
        configuration.setLogImpl(Slf4jImpl.class);
        if(PRE.equals(active) || PROD.equals(active)){
            configuration.setLogImpl(Slf4jImpl.class);
        }else {
            configuration.setLogImpl(StdOutImpl.class);
        }
        sqlSessionFactory.setConfiguration(configuration);
//        sqlSessionFactory.setPlugins(new Interceptor[]{
////    		performanceInterceptor(), //性能分析
//                // optimisticLockerInterceptor(),//乐观锁
//                paginationInterceptor(),//添加分页功能
//                auditDataInterceptor() // 自动审核
//
//        });
        // 自动寻找所有spring bean内的plugin
        Interceptor[] interceptors = interceptorsProvider.getIfAvailable();
        sqlSessionFactory.setGlobalConfig(xinsightGlobalConfiguration());
        sqlSessionFactory.setMapperLocations(resolveXinsightMapperLocations()); // 慧眼系统Mapper包路径
        return sqlSessionFactory.getObject();
    }

    /**
     * 慧眼系统Mapper文件路径
     */
    public Resource[] resolveXinsightMapperLocations() {
        ResourcePatternResolver resourceResolver = new PathMatchingResourcePatternResolver();
        List<String> mapperLocations = new ArrayList<>();
        mapperLocations.add("classpath*:xinsight/mapper/*.xml");
        List<Resource> resources = new ArrayList();
        if (!CollectionUtils.isEmpty(mapperLocations)) {
            for (String mapperLocation : mapperLocations) {
                try {
                    Resource[] mappers = resourceResolver.getResources(mapperLocation);
                    resources.addAll(Arrays.asList(mappers));
                } catch (IOException e) {
                    log.error("Get xinsight myBatis resources happened exception", e);
                }
            }
        }

        return resources.toArray(new Resource[resources.size()]);
    }
}
