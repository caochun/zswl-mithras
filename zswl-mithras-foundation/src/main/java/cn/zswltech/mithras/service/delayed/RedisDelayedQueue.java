package cn.zswltech.mithras.service.delayed;

import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBlockingQueue;
import org.redisson.api.RDelayedQueue;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2023/8/23 11:06
 */
@Component
@Slf4j
public class RedisDelayedQueue {

    @Autowired
    private RedissonClient redissonClient;

    /**
     * 添加对象进延时队列
     *
     * @param putInData 添加数据
     * @param delay     延时时间
     * @param timeUnit  时间单位
     * @param queueName 队列名称
     * @param <T>
     */
    private <T> void addQueue(T putInData, long delay, TimeUnit timeUnit, String queueName) {
        log.info("添加延迟队列,监听名称:{},时间:{},时间单位:{},内容:{}", queueName, delay, timeUnit, putInData);
        RBlockingQueue<T> blockingFairQueue = redissonClient.getBlockingQueue(queueName);
        RDelayedQueue<T> delayedQueue = redissonClient.getDelayedQueue(blockingFairQueue);
        delayedQueue.offer(putInData, delay, timeUnit);
    }

    /**
     * 添加队列-秒
     *
     * @param t     DTO传输类
     * @param delay 时间数量
     * @param <T>   泛型
     */
    public <T> void addQueueSeconds(T t, long delay, Class<? extends RedisDelayedQueueListener> clazz) {
        addQueue(t, delay, TimeUnit.SECONDS, clazz.getName());
    }

    /**
     * 添加队列-分
     *
     * @param t     DTO传输类
     * @param delay 时间数量
     * @param <T>   泛型
     */
    public <T> void addQueueMinutes(T t, long delay, Class<? extends RedisDelayedQueueListener> clazz) {
        addQueue(t, delay, TimeUnit.MINUTES, clazz.getName());
    }

    /**
     * 添加队列-时
     *
     * @param t     DTO传输类
     * @param delay 时间数量
     * @param <T>   泛型
     */
    public <T> void addQueueHours(T t, long delay, Class<? extends RedisDelayedQueueListener> clazz) {
        addQueue(t, delay, TimeUnit.HOURS, clazz.getName());
    }

    /**
     * 添加队列-天
     *
     * @param t     DTO传输类
     * @param delay 时间数量
     * @param <T>   泛型
     */
    public <T> void addQueueDays(T t, long delay, Class<? extends RedisDelayedQueueListener> clazz) {
        addQueue(t, delay, TimeUnit.DAYS, clazz.getName());
    }
}
