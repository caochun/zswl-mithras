package cn.zswltech.mithras.service.service.riskcontrol.eventbus.subscriber;

import cn.zswltech.mithras.service.enums.projreview.ProjRegionalClassify;
import cn.zswltech.mithras.riskcontrol.eventbus.MetricComputeEvent;
import cn.zswltech.mithras.riskcontrol.eventbus.SubscribeSupporter;
import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;


/**
 * 1.客户注册地省份为（山东、河北、江西、湖北、湖南、山西、四川、重庆）
 * 2.且风控行业分类不为（公用事业类、民生消费类、集团协同业务类）
 * R=满足以上条件「未核销本金之和」
 *
 * @author zhaozhengkang
 */
@Component
@Slf4j
public class MetricComputer32A10000396_JC047 extends RegionalRiskQuotaComputer
        implements SubscribeSupporter<MetricComputeEvent> {
    @Override
    public String getMetricCode() {
        return "A10000396_JC047";
    }

    @AllowConcurrentEvents
    @Subscribe
    @Override
    public void onSubscribe(MetricComputeEvent metricComputeEvent) {
        log.info("MetricComputeA10000396_JC047 onSubscribe");
        
        compute(metricComputeEvent);
    }

    @Override
    protected List<ProjRegionalClassify> getRegionalProjectClassify() {
        return Arrays.asList(ProjRegionalClassify.MODERATE_SUPPORT);
    }
}
