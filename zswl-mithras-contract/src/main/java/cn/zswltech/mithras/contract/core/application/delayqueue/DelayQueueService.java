package cn.zswltech.mithras.contract.core.application.delayqueue;


import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.service.event.timeout.TimeoutNotifyEvent;
import cn.zswltech.mithras.service.event.timeout.TimeoutStartEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author: jackerhe
 * @date: 2023/4/25 4:53 下午
 **/
@Slf4j
@Service
public class DelayQueueService {

    /*private int threadPoolSize = Runtime.getRuntime().availableProcessors() + 1;
    *//**
     * 创建一个最初为空的新 DelayQueue
     *//*
    private final DelayQueue<Task> queue = new DelayQueue<>();
    *//**
     * 线程池
     *//*
    private ExecutorService executor;
    *//**
     * 守护线程
     *//*
    private Thread daemonThread;
    private static boolean running;*/

    @Resource
    private RedisTemplate redisTemplate;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    public  static String HOST_ADDRESS;

    /**
     * 初始化守护线程
     */
    @PostConstruct
    public void init() {
        try {
            HOST_ADDRESS = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            log.info("获取地址信息错误", e);
            HOST_ADDRESS = "all_hosts";
        }
        log.info("===========DelayQueueService init {}=============", HOST_ADDRESS);
    }
   /* @PostConstruct
    public void init() {
        executor = Executors.newFixedThreadPool(threadPoolSize);
        running = true;
        daemonThread = new Thread(() -> execute());
        daemonThread.setDaemon(true);
        daemonThread.setName("Task Queue Daemon Thread");
        daemonThread.start();
        // reloadTask();
        log.info("===========Task Queue Daemon Thread start=============");
    }

    @PreDestroy
    public void destroy() {
        running = false;
        if (executor != null && !executor.isShutdown()) {
            executor.shutdown();
        }
        clearTaskInQueue();
        log.info("===========Task Queue Daemon Thread stop=============");
    }*/

    /**
     * 监听任务队列
     */
   /* private void execute() {
        while (running) {
            try {
                // 从延迟队列中取值,如果没有对象过期则队列一直等待，
                Task t1 = queue.take();
                if (t1 != null) {
                    // 修改任务的状态
                    TaskItem task = (TaskItem) t1.getTaskItem();
                    if (task == null) {
                        continue;
                    }
                    executor.execute(task);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.error("Delay Queue Thread error:{}", e.getMessage());
            }
        }
    }*/

    /**
     * 添加任务
     * @param event
     * @param
     */
    public void addTask(TimeoutStartEvent event) {
        if (event != null && event.getKey()!= null && event.getType() != null && !checkTime(event)) {
            putTask(generateKey(event.getType(), event.getKey()), event.getStartTime(),
                    event.getDuration(), event.isConver());
        }
    }

    private String generateKey(String type, String key){

        return String.join(":", HOST_ADDRESS, type, key);
    }

    /**
     * 加入队列
     * @param key 任务唯一标识
     * @param start 起始时间
     * @param duration 有效期，单位：秒
     */
    private void putTask(String key, long start, long duration, boolean isCover) {
        /*TaskItem task = new TaskItem(key, start, duration);
        task.setDelayQueueService(this);
        // 转换成ns
        long nanoTime = TimeUnit.NANOSECONDS.convert(duration, TimeUnit.SECONDS);
        // 创建一个任务
        Task<Runnable> k = new Task<>(nanoTime, task);
        // 将任务放在延迟的队列中
        if (queue.contains(k)) {
            if(!queue.remove(k)){
                log.info("DelayQueueService putTask del failed");
            }
        }
        queue.put(k);
      */
        //是否有必要加锁
        if(!isCover && redisTemplate.getExpire(key) > 0)
        {
            return;
        }
        log.info("DelayQueueService putTask update time key : {}, duration : {}", key, duration);
        redisTemplate.opsForValue().set(key,"",duration, TimeUnit.SECONDS);
    }



    /**
     * 删除任务
     * @param key
     * @param
     * @return
     */
    @SuppressWarnings("rawtypes")
    public void delTask(String type, String key) {
        if (type != null && key != null) {
           /* Task<Runnable> task =
                    new Task<Runnable>(new TaskItem(key));*/
            redisTemplate.delete(generateKey(type, key));
        }
    }

    /**
     * 清除队列中全部任务
     *//*
    public void clearTaskInQueue() {
        if (queue != null && !queue.isEmpty()) {
            queue.clear();
        }
    }*/

    public int getTaskCount(String pre) {
        Set keys = redisTemplate.keys(pre.concat("*"));
        return ObjectUtil.isNotEmpty(keys) ? keys.size() : 0;
    }

    /**
     * 任务到期通知
     * @param key
     * @param
     */
    @SuppressWarnings("rawtypes")
    public void sendNotifyEvent(String key) {
        if (key != null) {
            TimeoutNotifyEvent e = generateTimeoutNotifyEvent(key);
            if (e != null) {
                eventPublisher.publishEvent(e);
            }
        }
    }


    /**
     * 检查是否已到期
     * @param event
     * @return
     */
    private boolean checkTime(TimeoutStartEvent event) {
        boolean r = false;
        if (event != null) {
            long endTime = event.getStartTime() + event.getDuration() * 1000;
            long now = System.currentTimeMillis();
            // 已到期,不进队，直接发通知
            if (now > endTime) {
                r = true;
                TimeoutNotifyEvent notify = new TimeoutNotifyEvent (event.getSource(), event.getType(), event.getKey());
                eventPublisher.publishEvent(notify);
                log.debug("check time out, send notice msg:{}", notify.toString());
            }
        }
        return r;
    }

    @SuppressWarnings("rawtypes")
    private TimeoutNotifyEvent generateTimeoutNotifyEvent(String key) {
        String[] split = key.split(":");
        TimeoutNotifyEvent timeoutNotifyEvent = new TimeoutNotifyEvent(key, split[0], split[1]);
        return timeoutNotifyEvent;
    }

}
