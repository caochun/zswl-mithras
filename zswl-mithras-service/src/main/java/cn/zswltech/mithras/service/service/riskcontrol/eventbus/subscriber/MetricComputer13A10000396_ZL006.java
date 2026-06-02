package cn.zswltech.mithras.service.service.riskcontrol.eventbus.subscriber;

import cn.zswltech.mithras.service.mapper.model.riskcontrol.RiskControlStrategy;
import cn.zswltech.mithras.service.service.riskcontrol.AbstractMetricComputer;
import cn.zswltech.mithras.riskcontrol.eventbus.MetricComputeEvent;
import cn.zswltech.mithras.riskcontrol.eventbus.SubscribeSupporter;
import com.google.common.eventbus.AllowConcurrentEvents;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


/**
 * @author zhaozhengkang
 */
@Component
@Slf4j
public class MetricComputer13A10000396_ZL006 extends AbstractMetricComputer implements SubscribeSupporter<MetricComputeEvent> {

    @Override
    public String getMetricCode() {
        return "A10000396_ZL006";
    }

    @AllowConcurrentEvents
//    @Subscribe
    @Override
    public void onSubscribe(MetricComputeEvent metricComputeEvent) {
        log.info("MetricComputeA10000396_ZL006 onSubscribe");

        compute(metricComputeEvent);
    }

    @Override
    public void calculate(MetricComputeEvent event, RiskControlStrategy strategy) {

    }
}
