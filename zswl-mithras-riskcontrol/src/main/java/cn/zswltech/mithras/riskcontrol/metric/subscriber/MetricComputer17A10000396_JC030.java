package cn.zswltech.mithras.riskcontrol.metric.subscriber;

import cn.zswltech.mithras.foundation.enums.common.RiskControlIndustryClassify;
import cn.zswltech.mithras.riskcontrol.application.port.RiskControlClientFactPort;
import cn.zswltech.mithras.riskcontrol.application.port.RiskControlProjectReviewFactPort;
import cn.zswltech.mithras.riskcontrol.strategy.RiskControlStrategy;
import cn.zswltech.mithras.riskcontrol.metric.AbstractMetricComputer;
import cn.zswltech.mithras.riskcontrol.metric.MetricComputeEvent;
import cn.zswltech.mithras.riskcontrol.metric.SubscribeSupporter;
import com.alibaba.fastjson.JSON;
import com.google.common.eventbus.AllowConcurrentEvents;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;


/**
 * 1.风控行业分类不为（集团协同业务 / 船舶、光伏行业）
 * R=满足以上条件的合同内「报价方案-租赁期限（月）」/12
 *
 * @author zhaozhengkang
 */
@Component
@Slf4j
public class MetricComputer17A10000396_JC030 extends AbstractMetricComputer implements SubscribeSupporter<MetricComputeEvent> {
    @Resource
    private RiskControlClientFactPort clientFactPort;
    @Resource
    private RiskControlProjectReviewFactPort projectReviewFactPort;

    @Override
    public String getMetricCode() {
        return "A10000396_JC030";
    }

    @AllowConcurrentEvents
//    @Subscribe
    @Override
    public void onSubscribe(MetricComputeEvent metricComputeEvent) {
        log.info("MetricComputeA10000396_JC030 onSubscribe");
        compute(metricComputeEvent);
    }

    @Override
    public void calculate(MetricComputeEvent event, RiskControlStrategy strategy) {
        // 1.风控行业分类不为（船舶、光伏行业 和 集团协同业务）
        Set<Long> targetClients = clientFactPort.clientIdsNotInRiskControlIndustryClassify(new HashSet<>(Arrays.asList(
                RiskControlIndustryClassify.NEW_MATERIALS.name(),
                RiskControlIndustryClassify.INTRA_GROUP_COLLABORATION.name())));
        Integer maxMonthCount = projectReviewFactPort.maxReviewPriceMonthCountByClientIds(
                targetClients, event.getSnapshotDate());
        //5.保存
        strategy.setCurrentValueOne(maxMonthCount * 10000L / 12);
        MetricCompute_17Context context = new MetricCompute_17Context();
        context.setMaxMonthCount((long) maxMonthCount);
        strategy.setQuickContext(JSON.toJSONString(context));
    }

    @Data
    static class MetricCompute_17Context {
        private Long maxMonthCount;
    }
}
