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
 * 1.风控行业分类为「交通运输物流行业（含冷链仓储物流、普通物流等）」
 * R=满足以上条件的「未核销本金之和」
 * 风险策略调整，修改后的逻辑：
 * R=风控行业分类为「交通运输物流行业（含冷链仓储物流、汽车经销商、普通物流、公共交通等）」的「剩余本金之和 - 保证金余额」
 *
 * @author zhaozhengkang
 */
@Component
@Slf4j
public class MetricComputer22A10000396_JC036 extends RiskControlClassifyMetricComputer implements SubscribeSupporter<MetricComputeEvent> {
    @Override
    public String getMetricCode() {
        return "A10000396_JC036";
    }

    @Override
    public List<String> getIndustryClassify() {
        return Collections.singletonList(RiskControlIndustryClassify.TRANSPORTATION_LOGISTICS.name());
    }

    @AllowConcurrentEvents
    @Subscribe
    @Override
    public void onSubscribe(MetricComputeEvent metricComputeEvent) {
        log.info("MetricComputeA10000396_JC036 onSubscribe");
        
        compute(metricComputeEvent);
    }
}
