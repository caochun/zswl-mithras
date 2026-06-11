package cn.zswltech.mithras.foundation.cache;

import com.alibaba.fastjson.JSON;
import cn.zswltech.mithras.foundation.cache.RedisDelayedQueueListener;
import lombok.extern.slf4j.Slf4j;
import org.redisson.RedissonShutdownException;
import org.redisson.api.RBlockingQueue;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.Map;

import static cn.hutool.core.thread.ThreadUtil.sleep;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2023/8/23 11:05
 */
@Component
@Slf4j
public class RedisDelayedQueueInit implements ApplicationContextAware {

    @Autowired
    private RedissonClient redissonClient;

    /**
     * 获取应用上下文并获取相应的接口实现类
     *
     * @param applicationContext
     * @throws BeansException
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        Map<String, RedisDelayedQueueListener> map = applicationContext.getBeansOfType(RedisDelayedQueueListener.class);
        for (Map.Entry<String, RedisDelayedQueueListener> taskEventListenerEntry : map.entrySet()) {
            String listenerName = taskEventListenerEntry.getValue().getClass().getName();
            startThread(listenerName, taskEventListenerEntry.getValue());
            // 重新创建延时队列
            RBlockingQueue<Object> blockingQueue = redissonClient.getBlockingQueue(listenerName);
            redissonClient.getDelayedQueue(blockingQueue);
        }
    }

    /**
     * 启动线程获取队列
     *
     * @param queueName                 队列名称
     * @param redisDelayedQueueListener 任务回调监听
     */
    private <T> void startThread(String queueName, RedisDelayedQueueListener<T> redisDelayedQueueListener) {
        RBlockingQueue<T> blockingFairQueue = redissonClient.getBlockingQueue(queueName);
        //由于此线程需要常驻，可以新建线程，不用交给线程池管理
        Thread thread = new Thread(() -> {
            log.info("启动监听队列线程{}", queueName);
            while (!redissonClient.isShutdown() && !redissonClient.isShuttingDown()) {
                try {
                    T t = blockingFairQueue.take();
                    log.info("监听队列线程{},获取到值:{}", queueName, JSON.toJSONString(t));
                    redisDelayedQueueListener.invoke(t);
                } catch (RedissonShutdownException e) {
                    log.warn("Redisson客户端已关闭");
                    break;
                } catch (InterruptedException interruptedException) {
                    Thread.currentThread().interrupt();
                } catch (Exception e) {
                    sleep(3000);
                    log.error("监听队列线程错误,", e);
                }
            }
            log.info("关闭监听队列线程{}", queueName);
        });
        thread.setName(queueName);
        thread.start();
    }
}
