package cn.zswltech.mithras.system.infrastructure.config;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.task.TaskExecutionProperties;
import org.springframework.boot.task.TaskExecutorBuilder;
import org.springframework.boot.task.TaskExecutorCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.task.TaskDecorator;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.stream.Stream;

/**
 * 线程池配置
 *
 * @author wangchuanhao
 * @date 2022/6/21 2:58 PM
 */
@Configuration
public class ThreadPoolExecutorConfig {

    /**
     * 用于ocr识别发票信息
     *
     * @return
     */
    @Bean(name = "ocrThreadPool")
    public ThreadPoolTaskExecutor ocrThreadPoolTaskExecutor() {
        ThreadPoolTaskExecutor pool = new ThreadPoolTaskExecutor();
        pool.setKeepAliveSeconds(1);
        // 核心线程池数
        pool.setCorePoolSize(10);
        // 最大线程
        pool.setMaxPoolSize(10);
        // 队列容量
        pool.setQueueCapacity(1000);
        //队列满，线程被拒绝执行策略
        pool.setRejectedExecutionHandler(new java.util.concurrent.ThreadPoolExecutor.CallerRunsPolicy());
        return pool;
    }

    /**
     * 用于天眼查数据拉取
     *
     * @return
     */
    @Bean(name = "tycThreadPool")
    public ThreadPoolTaskExecutor tycThreadPoolTaskExecutor() {
        ThreadPoolTaskExecutor pool = new ThreadPoolTaskExecutor();
        pool.setKeepAliveSeconds(1);
        // 核心线程池数
        pool.setCorePoolSize(20);
        // 最大线程
        pool.setMaxPoolSize(20);
        // 队列容量
        pool.setQueueCapacity(1000);
        //队列满，线程被拒绝执行策略
        pool.setRejectedExecutionHandler(new java.util.concurrent.ThreadPoolExecutor.CallerRunsPolicy());
        return pool;
    }

    /**
     * 用于生成立项报告数据拉取
     *
     * @return
     */
    @Bean(name = "reportThreadPool")
    public ThreadPoolTaskExecutor reportThreadPoolTaskExecutor() {
        ThreadPoolTaskExecutor pool = new ThreadPoolTaskExecutor();
        pool.setKeepAliveSeconds(1);
        // 核心线程池数
        pool.setCorePoolSize(20);
        // 最大线程
        pool.setMaxPoolSize(20);
        // 队列容量
        // pool.setQueueCapacity(100);
        //队列满，线程被拒绝执行策略
        pool.setRejectedExecutionHandler(new java.util.concurrent.ThreadPoolExecutor.CallerRunsPolicy());
        return pool;
    }

    @Bean
    public TaskExecutorBuilder taskExecutorBuilder(TaskExecutionProperties properties, ObjectProvider<TaskExecutorCustomizer> taskExecutorCustomizers, ObjectProvider<TaskDecorator> taskDecorator) {
        TaskExecutionProperties.Pool pool = properties.getPool();
        TaskExecutorBuilder builder = new TaskExecutorBuilder();
        builder = builder.queueCapacity(pool.getQueueCapacity());
        builder = builder.corePoolSize(pool.getCoreSize());
        builder = builder.maxPoolSize(pool.getMaxSize());
        builder = builder.allowCoreThreadTimeOut(pool.isAllowCoreThreadTimeout());
        builder = builder.keepAlive(pool.getKeepAlive());
        TaskExecutionProperties.Shutdown shutdown = properties.getShutdown();
        builder = builder.awaitTermination(shutdown.isAwaitTermination());
        builder = builder.awaitTerminationPeriod(shutdown.getAwaitTerminationPeriod());
        builder = builder.threadNamePrefix(properties.getThreadNamePrefix());
        Stream var10001 = taskExecutorCustomizers.orderedStream();
        var10001.getClass();
        builder = builder.customizers(var10001::iterator);
        builder = builder.taskDecorator((TaskDecorator) taskDecorator.getIfUnique());
        return builder;
    }

    /**
     * 默认的executor
     *
     * @param builder
     * @return
     */
    @Lazy
    @Bean(name = {"applicationTaskExecutor", "taskExecutor"})
    public ThreadPoolTaskExecutor applicationTaskExecutor(TaskExecutorBuilder builder) {
        return builder.build();
    }
}
