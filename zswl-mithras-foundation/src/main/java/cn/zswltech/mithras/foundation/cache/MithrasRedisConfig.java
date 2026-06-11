package cn.zswltech.mithras.foundation.cache;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * redis配置
 *
 * @author wangchuanhao
 * @date 2022/10/17 12:35 PM
 */
@Configuration
@EnableCaching
public class MithrasRedisConfig extends CachingConfigurerSupport {

    @Value("${spring.redis.host}")
    private String hostName;
    @Value("${spring.redis.port}")
    private String port;
    @Value("${spring.redis.password}")
    private String password;
    @Value("${spring.redis.database}")
    private Integer database;

    @Bean("redisTemplate")
    @SuppressWarnings(value = { "unchecked", "rawtypes" })
    public RedisTemplate<Object, Object> redisTemplate(RedisConnectionFactory connectionFactory)
    {
        RedisTemplate<Object, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        template.setValueSerializer(new StringRedisSerializer());
        // 使用StringRedisSerializer来序列化和反序列化redis的key值
        template.setKeySerializer(new StringRedisSerializer());
        template.afterPropertiesSet();
        return template;
    }

    /**
     * @param redisTemplate
     * @return
     */
    @Bean(name = "redisHelper")
    RedisHelper redisHelper(RedisTemplate<String, String> redisTemplate) {
        RedisHelper redisHelper = new RedisHelper();
        redisHelper.setRedisTemplate(redisTemplate);
        return redisHelper;
    }

    @Bean(name = "redisDistLock")
    RedisDistLock redisDistLock() {
        RedisDistLock redisDistLock = new RedisDistLock();
        redisDistLock.setRedisHost(hostName);
        redisDistLock.setRedisPort(port);
        redisDistLock.setRedisPasswd(password);
        redisDistLock.setDatabase(database);
        redisDistLock.init();
        return redisDistLock;
    }

    /**
     * key过期事件订阅需要
     * @param redisConnectionFactory
     * @return
     */
    @Bean
    RedisMessageListenerContainer container(RedisConnectionFactory redisConnectionFactory) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(redisConnectionFactory);
        return container;
    }

}
