package cn.zswltech.mithras.riskcontrol.metric.subscriber;

import cn.zswltech.mithras.riskcontrol.application.port.RiskControlAssetClassifyPort;
import cn.zswltech.mithras.riskcontrol.exposure.RemainingPrincipalService;
import cn.zswltech.mithras.riskcontrol.strategy.RiskControlStrategy;
import cn.zswltech.mithras.riskcontrol.metric.AbstractMetricComputer;
import cn.zswltech.mithras.riskcontrol.exposure.RemainingPrincipalQueryDto;
import cn.zswltech.mithras.riskcontrol.metric.MetricComputeEvent;
import cn.zswltech.mithras.riskcontrol.metric.SubscribeSupporter;
import com.alibaba.fastjson.JSON;
import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;
import java.util.Optional;
import java.util.Set;


/**
 * R=五级分类后三类客户的剩余本金之和/所有剩余本金之和
 *
 * @author zhaozhengkang
 */
@Component
@Slf4j
public class MetricComputer5A10000396_JC015 extends AbstractMetricComputer implements SubscribeSupporter<MetricComputeEvent> {
    @Resource
    private RiskControlAssetClassifyPort riskControlAssetClassifyPort;
    @Resource
    private RemainingPrincipalService remainingPrincipalService;

    @Override
    public String getMetricCode() {
        return "A10000396_JC015";
    }

    @AllowConcurrentEvents
    @Subscribe
    @Override
    public void onSubscribe(MetricComputeEvent metricComputeEvent) {
        log.info("MetricComputeA10000396_JC015 onSubscribe");
        compute(metricComputeEvent);
    }

    @Override
    public void calculate(MetricComputeEvent event, RiskControlStrategy strategy) {
        Optional<Long> assetClassifyId = riskControlAssetClassifyPort.currentClassifyId(event.getSnapshotDate());
        if (!assetClassifyId.isPresent()) {
            strategy.setNullReason("当前无五级分类数据");
            strategy.setCurrentValueOne(null);
            strategy.setCurrentValueTwo(null);
            return;
        }
        //剩余本金之和
        BigDecimal remainingPrincipal = remainingPrincipalService.remainingPrincipal(event.getSnapshotDate());
        // 后三类剩余本金之和

        Set<Long> clientIds = riskControlAssetClassifyPort.lastThreeClassifyClientIds(assetClassifyId.get());
        RemainingPrincipalQueryDto dto = new RemainingPrincipalQueryDto();
        dto.setClientIds(clientIds);
        Map<Long, Long> longLongMap = remainingPrincipalService.remainingPrincipalGroupByClientId(dto);
        BigDecimal assetClassifyRemainingPrincipal = longLongMap.values().stream().map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add);
        // 计算
        BigDecimal resDecimal = assetClassifyRemainingPrincipal.divide(remainingPrincipal, 4, RoundingMode.HALF_UP);
        strategy.setCurrentValueOneDecimal(resDecimal);
        strategy.setQuickContext(new MetricCompute_2Context().setRemainingPrincipal(remainingPrincipal.toString()).setAssetClassifyRemainingPrincipal(assetClassifyRemainingPrincipal.toString()).toString());

    }

    /**
     * 客户是否属于资产分类后三类
     *
     * @param clientId 客户id
     * @return true: 属于 false: 不属于
     */
    public boolean isClientInLastThreeClassify(Long clientId) {
        return true;
    }

    @Data
    @Accessors(chain = true)
    static class MetricCompute_2Context {
        private String remainingPrincipal;
        private String assetClassifyRemainingPrincipal;

        @Override
        public String toString() {
            return JSON.toJSONString(this);
        }

        public MetricCompute_2Context deCode(String calculateCtx) {
            return JSON.parseObject(calculateCtx, MetricCompute_2Context.class);
        }
    }
}
