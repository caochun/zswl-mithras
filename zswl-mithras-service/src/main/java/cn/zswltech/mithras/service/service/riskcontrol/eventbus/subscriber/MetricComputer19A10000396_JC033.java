package cn.zswltech.mithras.service.service.riskcontrol.eventbus.subscriber;

import cn.zswltech.mithras.service.service.riskcontrol.eventbus.MetricComputeEvent;
import cn.zswltech.mithras.service.service.riskcontrol.eventbus.SubscribeSupporter;
import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

import static cn.zswltech.mithras.service.enums.riskcontrol.RiskControlIndustryClassify.PUBLIC_UTILITIES;


/**
 * 1.风控行业分类为「公用事业类」
 * R=满足以上条件的「未核销本金之和」
 *
 * @author zhaozhengkang
 */
@Component
@Slf4j
public class MetricComputer19A10000396_JC033 extends RiskControlClassifyMetricComputer implements SubscribeSupporter<MetricComputeEvent> {
    @Override
    public String getMetricCode() {
        return "A10000396_JC033";
    }

    @Override
    public List<String> getIndustryClassify() {
        return Collections.singletonList(PUBLIC_UTILITIES.name());
    }

    @AllowConcurrentEvents
    @Subscribe
    @Override
    public void onSubscribe(MetricComputeEvent metricComputeEvent) {
        log.info("MetricComputeA10000396_JC033 onSubscribe");
        compute(metricComputeEvent);
    }
}
