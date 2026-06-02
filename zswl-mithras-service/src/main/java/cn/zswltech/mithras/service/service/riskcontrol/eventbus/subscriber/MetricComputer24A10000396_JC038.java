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
 * 1.风控行业分类为「造纸行业」
 * R=满足以上条件的「未核销本金之和」
 *
 * @author zhaozhengkang
 * @deprecated 何栋要求删除（逻辑代码先保留）
 */
@Deprecated
//@Component
@Slf4j
public class MetricComputer24A10000396_JC038 extends RiskControlClassifyMetricComputer implements SubscribeSupporter<MetricComputeEvent> {
    @Override
    public String getMetricCode() {
        return "A10000396_JC038";
    }

    @Override
    public List<String> getIndustryClassify() {
        return Collections.singletonList(RiskControlIndustryClassify.PAPER_MAKING.name());
    }

    @AllowConcurrentEvents
    @Subscribe
    @Override
    public void onSubscribe(MetricComputeEvent metricComputeEvent) {
        log.info("MetricComputeA10000396_JC038 onSubscribe");
        
        compute(metricComputeEvent);
    }
}
