package cn.zswltech.mithras.riskcontrol.metric.subscriber;

import cn.zswltech.mithras.riskcontrol.metric.RiskMetricFactorQueryService;
import cn.zswltech.mithras.riskcontrol.metric.RiskMetricFactorValue;
import cn.zswltech.mithras.customer.mapper.lib.client.CorpCommerceInfoLibMapper;
import cn.zswltech.mithras.customer.mapper.model.client.CorpCommerceInfoLib;
import cn.zswltech.mithras.riskcontrol.strategy.RiskControlStrategy;
import cn.zswltech.mithras.riskcontrol.metric.AbstractMetricComputer;
import cn.zswltech.mithras.riskcontrol.exposure.RemainingPrincipalService;
import cn.zswltech.mithras.customer.application.lib.client.dto.CorpCommerceInfoLibDto;
import cn.zswltech.mithras.riskcontrol.exposure.RemainingPrincipalQueryDto;
import cn.zswltech.mithras.riskcontrol.metric.MetricComputeEvent;
import cn.zswltech.mithras.riskcontrol.metric.SubscribeSupporter;
import com.alibaba.fastjson.JSON;
import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


/**
 * 1.客户「是否关联方」为「是」的
 * R=按单个客户维度的剩余本金余额（取最大）/资产负债表「所有者权益（或股东权益）合计@期末余额」
 *
 * @author zhaozhengkang
 */
@Component
@Slf4j
public class MetricComputer10A10000396_JC049 extends AbstractMetricComputer implements SubscribeSupporter<MetricComputeEvent> {
    public static final String FACTOR_NAME = "所有者权益（或股东权益）合计@期末余额";
    public static final String FACTOR_TABLE = "资产负债表";
    @Resource
    private CorpCommerceInfoLibMapper corpCommerceInfoLibMapper;
    @Resource
    private RemainingPrincipalService remainingPrincipalService;
    @Resource
    private RiskMetricFactorQueryService factorService;

    @Override
    public String getMetricCode() {
        return "A10000396_JC049";
    }

    @AllowConcurrentEvents
    @Subscribe
    @Override
    public void onSubscribe(MetricComputeEvent metricComputeEvent) {
        log.info("MetricComputeA10000396_JC049 onSubscribe");

        compute(metricComputeEvent);
    }

    @Override
    public void calculate(MetricComputeEvent event, RiskControlStrategy strategy) {
        long maxRemainingPrincipal = 0L;
        RiskMetricFactorValue factor = factorService.newestFactor(FACTOR_NAME, FACTOR_TABLE, event.getFactorQueryDate());
        if (factor == null) {
            strategy.setNullReason("上月指标因子未导入");
            strategy.setCurrentValueOne(null);
            return;
        }
        CorpCommerceInfoLibDto dto = new CorpCommerceInfoLibDto();
        dto.setIsRelated(1);
        Set<Long> clientIds = corpCommerceInfoLibMapper.listNewestCommerceInfo(dto)
                .stream().map(CorpCommerceInfoLib::getClientId)
                .collect(Collectors.toSet());
        if (clientIds.isEmpty()) {
            strategy.setCurrentValueOne(0L);
            return;
        }
        RemainingPrincipalQueryDto queryDto = new RemainingPrincipalQueryDto();
        queryDto.setClientIds(clientIds);
        queryDto.setEndDate(event.getSnapshotDate());
        Map<Long, Long> clientIdToRemainingPrincipal =
                remainingPrincipalService.remainingPrincipalGroupByClientId(queryDto);
        for (Map.Entry<Long, Long> entry : clientIdToRemainingPrincipal.entrySet()) {
            maxRemainingPrincipal = Math.max(maxRemainingPrincipal, entry.getValue());
        }
        // 计算
        BigDecimal res = new BigDecimal(maxRemainingPrincipal)
                .divide(new BigDecimal(factor.getFactorValue()), 4, RoundingMode.HALF_UP)
                .multiply(new BigDecimal(10000));
        strategy.setCurrentValueOne(res.longValue());

        MetricCompute_10Context calculateCtx = new MetricCompute_10Context();
        calculateCtx.setClientIdToRemainingPrincipal(clientIdToRemainingPrincipal);
        calculateCtx.setMaxRemainingPrincipal(maxRemainingPrincipal);
        calculateCtx.setFactorValue(factor.getFactorValue());
        strategy.setQuickContext(JSON.toJSONString(calculateCtx));
    }

    @Data
    static class MetricCompute_10Context {
        private Map<Long, Long> clientIdToRemainingPrincipal;
        private Long maxRemainingPrincipal;
        private Long factorValue;
    }
}
