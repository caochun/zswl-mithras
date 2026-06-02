package cn.zswltech.mithras.service.service.riskcontrol.eventbus.subscriber;

import cn.zswltech.mithras.service.enums.riskcontrol.RiskControlIndustryClassify;
import cn.zswltech.mithras.riskcontrol.eventbus.MetricComputeEvent;
import cn.zswltech.mithras.riskcontrol.eventbus.SubscribeSupporter;
import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;


/**
 * 1.风控行业分类为「钢铁、不锈钢及有色金属冶炼行业」
 * R=满足以上条件的「未核销本金之和」
 *
 * @author zhaozhengkang
 */
@Component
@Slf4j
public class MetricComputer21A10000396_JC035 extends RiskControlClassifyMetricComputer implements SubscribeSupporter<MetricComputeEvent> {

    @Override
    public List<String> getIndustryClassify() {
        return Collections.singletonList(RiskControlIndustryClassify.STEEL.name());
    }

    @Override
    public String getMetricCode() {
        return "A10000396_JC035";
    }

    @AllowConcurrentEvents
    @Subscribe
    @Override
    public void onSubscribe(MetricComputeEvent metricComputeEvent) {
        log.info("MetricComputeA10000396_JC035 onSubscribe");
        
        compute(metricComputeEvent);
    }
}
