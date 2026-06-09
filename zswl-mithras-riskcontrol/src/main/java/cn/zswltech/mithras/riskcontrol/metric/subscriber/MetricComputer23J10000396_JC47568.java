package cn.zswltech.mithras.riskcontrol.metric.subscriber;

import cn.zswltech.mithras.riskcontrol.common.RiskControlIndustryClassify;
import cn.zswltech.mithras.riskcontrol.metric.MetricComputeEvent;
import cn.zswltech.mithras.riskcontrol.metric.SubscribeSupporter;
import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;


/**
 * 1.风控行业分类为「水上运输业，即航运行业」
 * R=满足以上条件的「未核销本金之和」
 *
 * @author zhaozhengkang
 */
@Component
@Slf4j
public class MetricComputer23J10000396_JC47568 extends RiskControlClassifyMetricComputer implements SubscribeSupporter<MetricComputeEvent> {
    @Override
    public String getMetricCode() {
        return "J10000396_JC47568";
    }

    @Override
    public List<String> getIndustryClassify() {
        return Collections.singletonList(RiskControlIndustryClassify.WATER_TRANSPORTATION.name());
    }

    @AllowConcurrentEvents
    @Subscribe
    @Override
    public void onSubscribe(MetricComputeEvent metricComputeEvent) {
        log.info("MetricComputeJ10000396_JC47568 onSubscribe");
        
        compute(metricComputeEvent);
    }
}
