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
 * 1.风控行业分类为「精细化工行业」
 * R=满足以上条件的「未核销本金之和」
 * 风险策略调整，修改后的逻辑：
 * R=风控行业分类为「造纸、精细化工、汽车零部件等传统制造行业」的「剩余本金之和 - 保证金余额」
 *
 * @author zhaozhengkang
 */
@Component
@Slf4j
public class MetricComputer25A10000396_JC039 extends RiskControlClassifyMetricComputer
        implements SubscribeSupporter<MetricComputeEvent> {
    @Override
    public String getMetricCode() {
        return "A10000396_JC039";
    }

    @Override
    public List<String> getIndustryClassify() {
        return Collections.singletonList(RiskControlIndustryClassify.PAPER_MAKING.name());
    }

    @AllowConcurrentEvents
    @Subscribe
    @Override
    public void onSubscribe(MetricComputeEvent metricComputeEvent) {
        log.info("MetricComputeA10000396_JC039 onSubscribe");
        
        compute(metricComputeEvent);
    }
}
