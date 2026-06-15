package cn.zswltech.mithras.foundation.cache;

import lombok.Setter;
import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.config.SingleServerConfig;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.concurrent.TimeUnit;

/**
 * redis锁
 *
 * @author wangchuanhao
 * @date 2022/10/17 12:35 PM
 */
@Setter
public class RedisDistLock {

    private String redisHost;
    private String redisPort;
    private String redisPasswd;
    private Integer database;

    private RedissonClient redissonClient;

    public void init() {

        Config config = new Config();
        SingleServerConfig singleServerConfig = config.useSingleServer()
            .setAddress("redis://" + redisHost + ":" + redisPort)
            .setDatabase(database);
        if (StringUtils.hasText(redisPasswd)) {
            singleServerConfig.setUsername("default").setPassword(redisPasswd);
        }
        redissonClient = Redisson.create(config);

    }

    /**
     * @param key
     * @param waitTime   等待时间单位毫秒
     * @param expireTime 锁自动失效时间 单位毫秒
     * @return
     */
    public boolean tryLock(String key, long waitTime, long expireTime) {
        RLock lock = redissonClient.getLock(key);
        try {
            return lock.tryLock(waitTime, expireTime, TimeUnit.MILLISECONDS);

        } catch (InterruptedException e) {
            throw new RuntimeException("try Get Distributed Lock fail");
        }

    }

    /**
     * 阻塞直到获得锁。
     * 获得锁之后可以手动释放，或经过指定时间之后自动释放
     *
     * @param key        锁对应的key
     * @param expireTime 锁自动失效时间 -1则自动续命
     */
    public void lock(String key, long expireTime) {
        RLock lock = redissonClient.getLock(key);
        lock.lock(expireTime, TimeUnit.MILLISECONDS);
    }

    /**
     * 不设置锁过期时间、锁自动续命、需要显示释放锁
     *
     * @param key
     * @param waitTime
     * @return
     */
    public boolean tryLockWithoutReleaseTime(String key, long waitTime) {
        RLock lock = redissonClient.getLock(key);
        try {
            return lock.tryLock(waitTime, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException("try Get Distributed Lock fail");
        }
    }


    public void unlock(String key) {
        RLock lock = redissonClient.getLock(key);
        lock.unlock();
    }

    public String buildBizKey(Object... params) {
        Assert.isTrue(params != null && params.length > 0, "参数不能为空");

        StringBuilder ans = new StringBuilder();
        for (Object param : params) {
            if (ans.length() != 0) {
                ans.append("#");
            }
            ans.append(String.valueOf(param));
        }

        return ans.toString();
    }


}
