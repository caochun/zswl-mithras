package cn.zswltech.mithras.others.service.riskcontrol;

import cn.zswltech.mithras.others.service.ApplicationTest;
import cn.zswltech.mithras.application.orchestration.riskcontrol.eventbus.MetricComputeEvent;
import cn.zswltech.mithras.application.orchestration.riskcontrol.eventbus.MetricComputeEventBus;
import org.junit.Test;

import javax.annotation.Resource;
import java.io.IOException;
import java.time.LocalDate;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2023/2/10 17:18
 */
public class EventBusTest extends ApplicationTest {
    @Resource
    private MetricComputeEventBus metricComputeEventBus;

    @Test
    public void test() {
        MetricComputeEvent metricComputeEvent = new MetricComputeEvent();
        metricComputeEvent.setSnapshotDate(LocalDate.now());
        metricComputeEventBus.post(metricComputeEvent);
        try {
            System.in.read();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println("test");
    }
}
