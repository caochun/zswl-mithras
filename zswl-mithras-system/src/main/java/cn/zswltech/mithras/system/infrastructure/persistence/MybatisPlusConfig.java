package cn.zswltech.mithras.system.infrastructure.persistence;

import cn.hutool.core.util.StrUtil;
import cn.zswltech.mithras.foundation.persistence.plugin.AuditDataInterceptor;
import cn.zswltech.mithras.foundation.persistence.plugin.CustomSqlInjector;
import cn.zswltech.mithras.system.infrastructure.persistence.interceptor.DeleteLogInterceptor;
import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceBuilder;
import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.MybatisXMLLanguageDriver;
import com.baomidou.mybatisplus.core.config.GlobalConfig;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.logging.slf4j.Slf4jImpl;
import org.apache.ibatis.logging.stdout.StdOutImpl;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.type.JdbcType;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
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
public class MybatisPlusConfig {

    @Value("${spring.profiles.active}")
    private String active;

    private static final String PRE = "pre";

    private static final String PROD = "prod";

    private static final String DEV_TEST = "dev";

    private static final String PROTEST = "protest";

    /**
     * mybatis-plus分页插件<br>
     */
    @Bean
    public PaginationInterceptor paginationInterceptor() {
        PaginationInterceptor paginationInterceptor = new PaginationInterceptor();
        paginationInterceptor.setLimit(10000L);
        return paginationInterceptor;
    }

    @Bean
    public AuditDataInterceptor auditDataInterceptor() {
        return new AuditDataInterceptor();
    }

    @Bean
    public DeleteLogInterceptor deleteLogInterceptor() {
        return new DeleteLogInterceptor();
    }

    @Bean
    public CustomSqlInjector myLogicSqlInjector() {
        return new CustomSqlInjector();
    }

    @Primary
    @Bean("mithrasDataSource")
    @ConfigurationProperties("spring.datasource.mithras")
    public DataSource mitharsDataSource() {
        return DruidDataSourceBuilder.create().build();
    }

    @Primary
    @Bean("mithrasTransactionManager")
    public DataSourceTransactionManager mithrasTransactionManager() {
        return new DataSourceTransactionManager(mitharsDataSource());
    }

    /**
     * 设置属性
     */
    @Bean("sqlSessionFactory")
    @Primary
    public SqlSessionFactory sqlSessionFactory(ObjectProvider<Interceptor[]> interceptorsProvider) throws Exception {
        MybatisSqlSessionFactoryBean sqlSessionFactory = new MybatisSqlSessionFactoryBean();
        sqlSessionFactory.setDataSource(mitharsDataSource());
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        MybatisConfiguration configuration = new MybatisConfiguration();
        configuration.setDefaultScriptingLanguage(MybatisXMLLanguageDriver.class);
        configuration.setJdbcTypeForNull(JdbcType.NULL);
        configuration.setMapUnderscoreToCamelCase(true); //驼峰标识
        configuration.setCacheEnabled(false);
        configuration.setCallSettersOnNulls(true);

//        //预发生产
//        if (StrUtil.equalsAny(active, PROD, PROTEST)) {
            configuration.setLogImpl(Slf4jImpl.class);
//        } else {
//            configuration.setLogImpl(StdOutImpl.class);
//        }
//        sqlSessionFactory.setPlugins(new Interceptor[]{
////    		performanceInterceptor(), //性能分析
//                // optimisticLockerInterceptor(),//乐观锁
//                paginationInterceptor(),//添加分页功能
//                auditDataInterceptor() // 自动审核
//
//        });
        // 自动寻找所有spring bean内的plugin
        Interceptor[] interceptors = interceptorsProvider.getIfAvailable();
//        Interceptor[] ss = Stream.of(interceptors).filter(interceptor -> !(interceptor instanceof DesensitizationIntercept)).toArray(Interceptor[]::new);
        sqlSessionFactory.setPlugins(interceptors);
        sqlSessionFactory.setGlobalConfig(globalConfiguration());
        sqlSessionFactory.setMapperLocations(resolveMapperLocations()); // Mapper包路径
        return sqlSessionFactory.getObject();
    }

    /**
     * 设置策略
     */
    @Bean
    @Primary
    public GlobalConfig globalConfiguration() {
        GlobalConfig conf = new GlobalConfig();
        GlobalConfig.DbConfig dbconf = new GlobalConfig.DbConfig();
        dbconf.setLogicDeleteValue("1");
        dbconf.setLogicNotDeleteValue("0");
        dbconf.setLogicDeleteField("deleted");
        conf.setDbConfig(dbconf);
        conf.setSqlInjector(this.myLogicSqlInjector());
        return conf;
    }

    public Resource[] resolveMapperLocations() {
        ResourcePatternResolver resourceResolver = new PathMatchingResourcePatternResolver();
        List<String> mapperLocations = new ArrayList<>();
        mapperLocations.add("classpath*:flow/mapper/*.xml");
        mapperLocations.add("classpath*:mapper/**/*.xml");
        mapperLocations.add("classpath*:mybatis/mapper/*.xml");
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


