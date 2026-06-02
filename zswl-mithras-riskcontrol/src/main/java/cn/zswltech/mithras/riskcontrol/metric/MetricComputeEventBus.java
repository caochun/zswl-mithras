package cn.zswltech.mithras.riskcontrol.metric;

import com.google.common.eventbus.AsyncEventBus;
import com.google.common.eventbus.EventBus;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor.CallerRunsPolicy;
import java.util.concurrent.TimeUnit;

/**
 * @author zhaozhengkang
 */
@Component
public class MetricComputeEventBus {

    private final static Logger log = LoggerFactory.getLogger(MetricComputeEventBus.class);

    /**
     * 线程池大小设置成核心数 - 1
     */
    private final ExecutorService EXECUTOR = new ThreadPoolExecutor(
            Runtime.getRuntime().availableProcessors() - 1,
            2 * Runtime.getRuntime().availableProcessors(), 30L, TimeUnit.MILLISECONDS,
            new LinkedBlockingDeque<Runnable>(4096),
            new BasicThreadFactory.Builder().namingPattern("metric-compute-task-pool-%d").build(),
            new CallerRunsPolicy());

    private final EventBus asyncEventBus = new AsyncEventBus("metric-compute-event-bus", EXECUTOR);

    public void post(SubscribeEvent event) {
        asyncEventBus.post(event);
    }

    public void register(Object object, String type) {
        try {
            asyncEventBus.register(object);
        } catch (Exception e) {
            log.error("register event bus error in {}", type, e);
        }
    }

    public void unregister(Object object, String type) {
        try {
            asyncEventBus.unregister(object);
            log.info("unregister event bus success in {}", type);
        } catch (Exception e) {
            log.error("unregister event bus error in {}", type, e);
        }
    }

}
