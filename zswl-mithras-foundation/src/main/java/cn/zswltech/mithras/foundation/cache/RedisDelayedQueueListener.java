package cn.zswltech.mithras.foundation.cache;

/**
 * @description: redis 队列事件监听,需要实现这个方法
 * @author: zhaozhengkang
 * @date: 2023/8/23 10:56
 */
public interface RedisDelayedQueueListener<T> {

    /**
     * 执行方法
     *
     * @param t
     */
    void invoke(T t);
}
