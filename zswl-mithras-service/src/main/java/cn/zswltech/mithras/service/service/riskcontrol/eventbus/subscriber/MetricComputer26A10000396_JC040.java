package cn.zswltech.mithras.service.service.riskcontrol.eventbus.subscriber;

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
 * 1.风控行业分类为「建筑工程行业」
 * R=满足以上条件的「未核销本金之和」
 * 风险策略调整，修改后的逻辑：
 * R=风控行业分类为「建筑工程行业（含建筑材料）」的「剩余本金之和 - 保证金余额」
 *
 * @author zhaozhengkang
 */
@Component
@Slf4j
public class MetricComputer26A10000396_JC040 extends RiskControlClassifyMetricComputer
        implements SubscribeSupporter<MetricComputeEvent> {

    @Override
    public String getMetricCode() {
        return "A10000396_JC040";
    }

    @Override
    public List<String> getIndustryClassify() {
        return Collections.singletonList(RiskControlIndustryClassify.CONSTRUCTION.name());
    }

    @AllowConcurrentEvents
    @Subscribe
    @Override
    public void onSubscribe(MetricComputeEvent metricComputeEvent) {
        log.info("MetricComputeA10000396_JC040 onSubscribe");
        
        compute(metricComputeEvent);
    }
}
