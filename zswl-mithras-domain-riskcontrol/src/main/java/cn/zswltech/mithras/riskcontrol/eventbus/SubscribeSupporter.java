package cn.zswltech.mithras.riskcontrol.eventbus;

import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;

/**
 * @author zhaozhengkang
 */
public interface SubscribeSupporter<E extends SubscribeEvent> {
    /**
     * 接收订阅事件处理
     * <p>
     * 实现类需要加上{@link Subscribe}注解 和{@link AllowConcurrentEvents}(如果要支持并行)
     */
    void onSubscribe(E event);
}
