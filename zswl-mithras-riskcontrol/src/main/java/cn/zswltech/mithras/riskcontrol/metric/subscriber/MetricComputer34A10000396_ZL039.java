package cn.zswltech.mithras.riskcontrol.metric.subscriber;

import cn.zswltech.mithras.foundation.enums.common.RiskControlIndustryClassify;
import cn.zswltech.mithras.riskcontrol.metric.MetricComputeEvent;
import cn.zswltech.mithras.riskcontrol.metric.SubscribeSupporter;
import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;


/**
 * 1.风控行业分类为「精细化工行业（非长三角地区）」
 * R=满足以上条件的「未核销本金之和」
 *
 * @author zhaozhengkang
 * @deprecated 何栋要求删除（逻辑代码先保留）
 */
@Deprecated
//@Component
@Slf4j
public class MetricComputer34A10000396_ZL039 extends RiskControlClassifyMetricComputer
        implements SubscribeSupporter<MetricComputeEvent> {
    @Override
    public String getMetricCode() {
        return "A10000396_ZL039";
    }

    @Override
    public List<String> getIndustryClassify() {
        return Collections.singletonList(RiskControlIndustryClassify.PAPER_MAKING.name());
    }

    @AllowConcurrentEvents
    @Subscribe
    @Override
    public void onSubscribe(MetricComputeEvent metricComputeEvent) {
        log.info("MetricComputeA10000396_ZL039 onSubscribe");
        compute(metricComputeEvent);
    }
}
