package cn.zswltech.mithras.riskcontrol.metric.subscriber;

import cn.zswltech.mithras.riskcontrol.common.RiskControlIndustryClassify;
import cn.zswltech.mithras.riskcontrol.metric.MetricComputeEvent;
import cn.zswltech.mithras.riskcontrol.metric.SubscribeSupporter;
import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;


/**
 * 1.风控行业分类为「民生消费类（含供水供热供电供气、污水处理、公共交通等）」和[旅游行业]
 * R=满足以上条件的「未核销本金之和」
 * 风险策略调整，修改后的逻辑：
 * R=风控行业分类为「民生消费类（含供水供热供电供气、污水处理、旅游行业等）」的「剩余本金之和 - 保证金余额」
 *
 * @author zhaozhengkang
 */
@Component
@Slf4j
public class MetricComputer20A10000396_JC034 extends RiskControlClassifyMetricComputer implements SubscribeSupporter<MetricComputeEvent> {
    @Override
    public List<String> getIndustryClassify() {
        return Arrays.asList(RiskControlIndustryClassify.CIVIL_CONSUMPTION.name(), RiskControlIndustryClassify.TRAVEL.name());
    }

    @Override
    public String getMetricCode() {
        return "A10000396_JC034";
    }

    @AllowConcurrentEvents
    @Subscribe
    @Override
    public void onSubscribe(MetricComputeEvent metricComputeEvent) {
        log.info("MetricComputeA10000396_JC034 onSubscribe");
        
        compute(metricComputeEvent);
    }
}
