package cn.zswltech.mithras.foundation.cache;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.types.Expiration;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * redis操作
 *
 * @author wangchuanhao
 * @date 2022/10/17 12:35 PM
 */
@Slf4j
@Component
public class RedisHelper {

    private RedisTemplate<String, String> redisTemplate;

    public Long getExpireTime(String key) {
        return redisTemplate.getExpire(key);
    }

    /**
     * 写入redis，设置过期时间，单位秒
     *
     * @param key
     * @param value
     * @param time
     */
    public void setStringObjectSecond(String key, String value, long time) {

        redisTemplate.opsForValue().set(key, value, time, TimeUnit.SECONDS);
    }

    /**
     * 去读redis
     *
     * @param key
     * @return
     */
    public String getStringObject(String key) {

        Object result = redisTemplate.opsForValue().get(key);

        return result == null ? null : result.toString();
    }

    /**
     * 判断是否存在
     *
     * @param key
     * @return
     */
    public boolean exists(String key) {

        return redisTemplate.hasKey(key);
    }

    /**
     * 删除
     *
     * @param key
     */
    public void delete(String key) {
        if (exists(key)) {
            redisTemplate.delete(key);
        }
    }

    /**
     * 读取字符串缓存
     *
     * @param key
     * @return
     */
    public String getString(final String key) {
        Object result = null;
        ValueOperations<String, String> operations = redisTemplate.opsForValue();
        result = operations.get(key);
        return (String) result;
    }

    /**
     * 设置字符串缓存
     *
     * @param key
     * @return
     */
    public void putString(final String key, String value) {
        redisTemplate.opsForValue().set(key,value);
    }

    public void setRedisTemplate(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void expire(String key, long i) {
        redisTemplate.expire(key,i,TimeUnit.SECONDS);
    }

    /**
     * 将 key 中储存的数字值增加。
     *
     * 如果 key 不存在，那么 key 的值会先被初始化为 num ，然后再执行 INCR 操作。
     *
     * @param key
     * @param num
     * @return
     */
    public Integer incrBy(final String key, final Long num, final long expireTime) {
        ValueOperations<String, String> valueOper
                = redisTemplate.opsForValue();

        int count = valueOper.increment(key, num).intValue();
        expire(key, expireTime);
        return count;
    }

    public Integer incrBy(final String key, final Long num) {
        ValueOperations<String, String> valueOper
                = redisTemplate.opsForValue();

        int count = valueOper.increment(key, num).intValue();
        return count;
    }

    public List<String> getStringList(String key) {
        String listStr = this.getString(key);
        if (StringUtils.isEmpty(listStr)) {
            return null;
        }
        List<String> qrCodeKeyList = JSONArray.parseArray(listStr, String.class);
        if (CollectionUtils.isEmpty(qrCodeKeyList)) {
            return null;
        }
        return qrCodeKeyList;
    }

    /**
     * 查询, 如果不存在则创建
     *      设置默认值
     * @param key        key
     * @param supplier   supplier
     * @param expireTime 过期时间
     * @return 查询到的值或者表达式中的值
     */
    public <T extends Object> T getCreatObj(String key, Supplier<T> supplier, long expireTime, Class classz){

        String cached = getString(key);
        if (StringUtils.isNotEmpty(cached)) {
            if (cached.equals("null")) {
                return null;
            }
            return (T)JSON.parseObject(cached,classz);
        }

        if (supplier == null) {
            return null;
        }

        T data = supplier.get();
        if (data == null) {
            setStringObjectSecond(key,"null",expireTime);
            return null;
        }

        setStringObjectSecond(key, JSON.toJSONString(data), expireTime);
        return data;
    }

    /**
     * set添加元素
     *
     * @param key
     * @param values
     * @return
     */
    public Long addSet(String key, String... values) {
        return redisTemplate.opsForSet().add(key, values);
    }

    /**
     * 往list中添加元素
     * @param key
     * @param value
     * @param expireTime
     * @return
     */
    public Long addListWithTime(String key ,String value,Long expireTime){
        Long aLong = redisTemplate.opsForList().rightPush(key,  value);
        redisTemplate.expire(key,expireTime,TimeUnit.SECONDS);
        return aLong;
    }

    /**
     * 获取所有的list
     * @param key
     */
    public List<String> getList(String key){
        return  redisTemplate.opsForList().range(key, 0, -1);
    }

    public List<String> getList(String key, Long start, Long end) {
        return redisTemplate.opsForList().range(key, start, end);
    }

    public Long getListSize(String key) {
        return redisTemplate.opsForList().size(key);
    }

    public Long rightPushString(final String key, String value, Long expireTime) {
        ListOperations operations = redisTemplate.opsForList();
        Long push = operations.rightPush(key, value);
        this.expire(key, expireTime);
        return push;
    }

    public Long rightPushStringList(final String key, List<String> value, Long expireTime) {
        ListOperations operations = redisTemplate.opsForList();
        Long push = operations.rightPushAll(key, value);
        this.expire(key, expireTime);
        return push;
    }

    public String leftPop(String key) {
        return (String) redisTemplate.opsForList().leftPop(key);
    }

    public void putAllMap(String key, Map map, Long expireTime) {
        HashOperations hashOperations = redisTemplate.opsForHash();
        hashOperations.putAll(key, map);
        this.expire(key, expireTime);
    }

    public Long increaseMapKey(String key, String mapKey, Long times) {
        HashOperations hashOperations = redisTemplate.opsForHash();
        return hashOperations.increment(key, mapKey, times);
    }

    public Boolean existMapKey(String key, String mapKey) {
        HashOperations hash = redisTemplate.opsForHash();
        return hash.hasKey(key, mapKey);
    }

    public void putSingleMap(String key, String mapKey, String mapValue, Long expireTime) {
        HashOperations hash = redisTemplate.opsForHash();
        hash.put(key, mapKey, mapValue);
        this.expire(key, expireTime);
    }

    public Map<String, String> getMap(String redisKey) {
        HashOperations<String, String, String> operations = redisTemplate.opsForHash();
        return operations.entries(redisKey);
    }

    public String getMapElement(String redisKey, String mapKey) {
        HashOperations<String, String, String> operations = redisTemplate.opsForHash();
        return operations.get(redisKey, mapKey);
    }

    /**
     * 将set数据放入缓存
     *
     * @param key   键
     * @param time   时间(秒)
     * @param value  值
     * @return 成功个数
     */
    public long sSetAndTime(String key, long time, Object value) {
        try {
            Long count = redisTemplate.opsForSet().add(key, value.toString());
            if (time > 0){
                expire(key, time);
            }
            return count;
        } catch (Exception e) {
            log.error("redis操作错误sSetAndTime, key:{}", key, e);
            return 0;
        }
    }
    /**
     * set移除元素
     *
     * @param key
     * @param value
     * @return
     */
    public Long sRemove(String key, Object value ) {
        return redisTemplate.opsForSet().remove(key, value.toString());
    }

    /**
     * 获取集合所有元素
     *
     * @param key
     * @return
     */
    public Set<Object> getSetMembers(String key) {
        Set<String> members = redisTemplate.opsForSet().members(key);
        if (org.apache.commons.collections4.CollectionUtils.isEmpty(members)) {
            return new HashSet<>();
        }
        Object[] objects = members.toArray();
        return new HashSet<>(Arrays.asList(objects));
    }

    /**
     * setEx
     * @param key
     * @param seconds
     * @return
     */
    public boolean setNX(String key, String value, long seconds) {
        try {
            return redisTemplate.execute((RedisCallback<Boolean>) connection -> {
                Boolean acquire = connection.set(key.getBytes(), value.getBytes(), Expiration.seconds(seconds), RedisStringCommands.SetOption.SET_IF_ABSENT);
                return acquire;
            });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
