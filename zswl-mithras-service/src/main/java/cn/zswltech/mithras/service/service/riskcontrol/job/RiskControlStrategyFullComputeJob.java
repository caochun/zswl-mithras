package cn.zswltech.mithras.service.service.riskcontrol.job;

import cn.zswltech.mithras.riskcontrol.eventbus.MetricComputeEvent;
import cn.zswltech.mithras.riskcontrol.eventbus.MetricComputeEventBus;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDate;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2023/2/16 22:29
 */
@Slf4j
@Component
public class RiskControlStrategyFullComputeJob {
    @Resource
    private MetricComputeEventBus metricComputeEventBus;

    @XxlJob("fullComputeMetricJobHandler")
    public void fullComputeMetricJobHandler() {
        log.info("fullComputeMetricJobHandler");
        try {
            MetricComputeEvent metricComputeEvent = new MetricComputeEvent();
            metricComputeEvent.setSnapshotDate(LocalDate.now());
            metricComputeEventBus.post(metricComputeEvent);
        } catch (Throwable t) {
            log.error("fullComputeMetricJobHandler error", t);
        }
    }

}
