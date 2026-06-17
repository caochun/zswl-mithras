package cn.zswltech.mithras.riskcontrol.metric.subscriber;

import cn.zswltech.mithras.riskcontrol.common.RegionalProjectClassify;
import cn.zswltech.mithras.riskcontrol.metric.MetricComputeEvent;
import cn.zswltech.mithras.riskcontrol.metric.SubscribeSupporter;
import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;


/**
 * 1.客户注册地省份为（北京、上海、浙江、江苏、福建、广东、安徽）
 * 2.且风控行业分类不为（公用事业类、民生消费类、集团协同业务类）
 * R=满足以上条件「未核销本金之和」
 *
 * @author zhaozhengkang
 */
@Component
@Slf4j
public class MetricComputer31A10000396_JC046 extends RegionalRiskQuotaComputer
        implements SubscribeSupporter<MetricComputeEvent> {
    @Override
    public String getMetricCode() {
        return "A10000396_JC046";
    }

    @AllowConcurrentEvents
    @Subscribe
    @Override
    public void onSubscribe(MetricComputeEvent metricComputeEvent) {
        log.info("MetricComputeA10000396_JC046 onSubscribe");

        compute(metricComputeEvent);
    }

    @Override
    protected List<RegionalProjectClassify> getRegionalProjectClassify() {
        return Arrays.asList(RegionalProjectClassify.ENCOURAGE_SUPPORT, RegionalProjectClassify.ZHEJIANG);
    }
}
