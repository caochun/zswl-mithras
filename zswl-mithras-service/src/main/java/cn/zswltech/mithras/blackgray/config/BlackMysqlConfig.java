
package cn.zswltech.mithras.blackgray.config;

import cn.zswltech.mithras.service.plugin.CustomSqlInjector;
import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.MybatisXMLLanguageDriver;
import com.baomidou.mybatisplus.core.config.GlobalConfig;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.logging.slf4j.Slf4jImpl;
import org.apache.ibatis.logging.stdout.StdOutImpl;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.type.JdbcType;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import tk.mybatis.spring.annotation.MapperScan;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Configuration
@Slf4j
@MapperScan(value = "cn.zswltech.mithras.blackgray.mapper", sqlSessionFactoryRef = "blackSqlSessionFactory")
public class BlackMysqlConfig {

    @Autowired
    private CustomSqlInjector customSqlInjector;
    @Autowired
    private DataSource mithrasDataSource;

    @Value("${spring.profiles.active}")
    private String active;

    private static final String PRE = "pre";

    private static final String PROD = "prod";


    @Bean("blackGlobalConfiguration")
    public GlobalConfig blackGlobalConfiguration() {
        GlobalConfig conf = new GlobalConfig();
        GlobalConfig.DbConfig dbconf = new GlobalConfig.DbConfig();
        dbconf.setLogicDeleteValue("1");
        dbconf.setLogicNotDeleteValue("0");
        dbconf.setLogicDeleteField("deleted");
        conf.setDbConfig(dbconf);
        conf.setSqlInjector(customSqlInjector);
        return conf;
    }


    @Bean("blackSqlSessionFactory")
    public SqlSessionFactory blackSqlSessionFactory(ObjectProvider<Interceptor[]> interceptorsProvider) throws Exception {
        MybatisSqlSessionFactoryBean sqlSessionFactory = new MybatisSqlSessionFactoryBean();
        sqlSessionFactory.setDataSource(mithrasDataSource);
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
        // 自动寻找所有spring bean内的plugin
        sqlSessionFactory.setPlugins(interceptorsProvider.getIfAvailable());
        sqlSessionFactory.setGlobalConfig(blackGlobalConfiguration());
        sqlSessionFactory.setMapperLocations(resolveMapperLocations()); // Mapper包路径
        return sqlSessionFactory.getObject();
    }

    public Resource[] resolveMapperLocations() {
        ResourcePatternResolver resourceResolver = new PathMatchingResourcePatternResolver();
        List<String> mapperLocations = new ArrayList<>();
        mapperLocations.add("classpath*:blackgray/mapper/*.xml");
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



