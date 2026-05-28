package cn.zswltech.mithras.service.service.riskcontrol.eventbus.subscriber;

import cn.zswltech.mithras.metric.mapper.model.RiskMetricFactor;
import cn.zswltech.mithras.metric.service.RiskMetricFactorService;
import cn.zswltech.mithras.service.mapper.lib.client.CorpCommerceInfoLibMapper;
import cn.zswltech.mithras.service.mapper.model.client.CorpCommerceInfoLib;
import cn.zswltech.mithras.service.mapper.model.riskcontrol.RiskControlStrategy;
import cn.zswltech.mithras.service.service.riskcontrol.AbstractMetricComputer;
import cn.zswltech.mithras.service.service.riskcontrol.RemainingPrincipalServiceImpl;
import cn.zswltech.mithras.service.service.riskcontrol.dto.CorpCommerceInfoLibDto;
import cn.zswltech.mithras.service.service.riskcontrol.dto.RemainingPrincipalQueryDto;
import cn.zswltech.mithras.service.service.riskcontrol.eventbus.MetricComputeEvent;
import cn.zswltech.mithras.service.service.riskcontrol.eventbus.SubscribeSupporter;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
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
    private RemainingPrincipalServiceImpl remainingPrincipalServiceImpl;
    @Resource
    private RiskMetricFactorService factorService;

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
        RiskMetricFactor factor = factorService.getOne(Wrappers.<RiskMetricFactor>lambdaQuery()
                .eq(RiskMetricFactor::getFactorName, FACTOR_NAME)
                .eq(RiskMetricFactor::getFactorTable, FACTOR_TABLE)
                .ge(RiskMetricFactor::getFactorDate, event.getFactorQueryDate())
                .orderByDesc(RiskMetricFactor::getFactorDate)
                .last("limit 1"));
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
                remainingPrincipalServiceImpl.remainingPrincipalGroupByClientId(queryDto);
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
