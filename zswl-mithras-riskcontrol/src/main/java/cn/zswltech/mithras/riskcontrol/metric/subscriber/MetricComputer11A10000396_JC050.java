package cn.zswltech.mithras.riskcontrol.metric.subscriber;

import cn.zswltech.mithras.riskcontrol.metric.RiskMetricFactorQueryService;
import cn.zswltech.mithras.riskcontrol.metric.RiskMetricFactorValue;
import cn.zswltech.mithras.customer.mapper.lib.client.CorpCommerceInfoLibMapper;
import cn.zswltech.mithras.customer.mapper.model.client.CorpCommerceInfoLib;
import cn.zswltech.mithras.riskcontrol.strategy.RiskControlStrategy;
import cn.zswltech.mithras.riskcontrol.metric.AbstractMetricComputer;
import cn.zswltech.mithras.riskcontrol.exposure.RemainingPrincipalService;
import cn.zswltech.mithras.customer.application.riskcontrol.dto.CorpCommerceInfoLibDto;
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
 * 客户「是否关联方」为「是」的
 * R=全部关联方客户的剩余本金余额之和/资产负债表「所有者权益（或股东权益）合计@期末余额」
 *
 * @author zhaozhengkang
 */
@Component
@Slf4j
public class MetricComputer11A10000396_JC050 extends AbstractMetricComputer implements SubscribeSupporter<MetricComputeEvent> {
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
        return "A10000396_JC050";
    }

    @AllowConcurrentEvents
    @Subscribe
    @Override
    public void onSubscribe(MetricComputeEvent metricComputeEvent) {
        log.info("MetricComputeA10000396_JC050 onSubscribe");

        compute(metricComputeEvent);
    }

    @Override
    public void calculate(MetricComputeEvent event, RiskControlStrategy strategy) {
        RiskMetricFactorValue factor = factorService.newestFactor(FACTOR_NAME, FACTOR_TABLE, event.getFactorQueryDate());
        if (factor == null) {
            strategy.setNullReason("上月指标因子未导入");
            strategy.setCurrentValueOne(null);
            strategy.setCurrentValueTwo(null);
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
        BigDecimal totalRemainingPrincipal = clientIdToRemainingPrincipal.values().stream().map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add);

        // 计算新值结果保留两位小数
        BigDecimal resDecimal = totalRemainingPrincipal
                .divide(new BigDecimal(factor.getFactorValue()), 4, RoundingMode.HALF_UP);
        strategy.setCurrentValueOneDecimal(resDecimal);
        MetricCompute_11Context calculateCtx = new MetricCompute_11Context();
        calculateCtx.setFactorValue(factor.getFactorValue());
        calculateCtx.setTotalRemainingPrincipal(totalRemainingPrincipal.toString());
        strategy.setQuickContext(JSON.toJSONString(calculateCtx));
    }

    @Data
    static class MetricCompute_11Context {
        private Long factorValue;
        private String totalRemainingPrincipal;
    }
}
