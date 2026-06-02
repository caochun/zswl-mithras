package cn.zswltech.mithras.service.service.riskcontrol.eventbus.subscriber;

import cn.zswltech.mithras.metric.mapper.model.RiskMetricFactor;
import cn.zswltech.mithras.metric.service.RiskMetricFactorService;
import cn.zswltech.mithras.service.mapper.model.assetclassify.AssetClassify;
import cn.zswltech.mithras.service.mapper.model.assetclassify.AssetClassifyClient;
import cn.zswltech.mithras.service.mapper.model.riskcontrol.RiskControlStrategy;
import cn.zswltech.mithras.service.service.assetclassify.AssetClassifyService;
import cn.zswltech.mithras.service.service.lib.assetclassify.AssetClassifyClientAuxiliaryLibService;
import cn.zswltech.mithras.service.service.riskcontrol.AbstractMetricComputer;
import cn.zswltech.mithras.service.service.riskcontrol.RemainingPrincipalServiceImpl;
import cn.zswltech.mithras.service.service.riskcontrol.dto.RemainingPrincipalQueryDto;
import cn.zswltech.mithras.riskcontrol.eventbus.MetricComputeEvent;
import cn.zswltech.mithras.riskcontrol.eventbus.SubscribeSupporter;
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
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;


/**
 * R=科目余额表「坏账准备_长期应收款坏账准备@期末余额@贷方金额」/五级分类后三类的剩余本金之和
 *
 * @author zhaozhengkang
 */
@Component
@Slf4j
public class MetricComputer2A10000396_JC017 extends AbstractMetricComputer implements SubscribeSupporter<MetricComputeEvent> {
    @Resource
    private RiskMetricFactorService factorService;
    @Resource
    private AssetClassifyService assetClassifyService;
    @Resource
    private AssetClassifyClientAuxiliaryLibService assetClassifyClientAuxiliaryLibService;
    @Resource
    private RemainingPrincipalServiceImpl remainingPrincipalServiceImpl;

    public static final String FACTOR_NAME = "坏账准备_长期应收款坏账准备@期末余额@贷方金额";
    public static final String FACTOR_TABLE = "科目余额表";

    @Override
    public String getMetricCode() {
        return "A10000396_JC017";
    }

    @AllowConcurrentEvents
    @Subscribe
    @Override
    public void onSubscribe(MetricComputeEvent metricComputeEvent) {
        log.info("MetricComputeA10000396_JC017 onSubscribe");
        compute(metricComputeEvent);
    }

    @Override
    public void calculate(MetricComputeEvent event, RiskControlStrategy strategy) {
        Optional<AssetClassify> assetClassify = assetClassifyService
                .currentClassify(event.getSnapshotDate());
        if (!assetClassify.isPresent()) {
            strategy.setNullReason("当前无五级分类数据");
            strategy.setCurrentValueOne(null);
            strategy.setCurrentValueTwo(null);
            return;
        }
        Set<Long> clientIds = assetClassifyClientAuxiliaryLibService.lastThreeNewestClassifyClientLib(assetClassify.get().getId()).stream()
                .map(AssetClassifyClient::getClientId).collect(Collectors.toSet());
        RemainingPrincipalQueryDto dto = new RemainingPrincipalQueryDto();
        dto.setClientIds(clientIds);
        dto.setEndDate(event.getSnapshotDate());
        Map<Long, Long> principalMap = remainingPrincipalServiceImpl.remainingPrincipalGroupByClientId(dto);
        BigDecimal totalRemainingPrincipal = principalMap.values().stream().map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add);

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
        if (totalRemainingPrincipal.compareTo(BigDecimal.ZERO) == 0) {
            strategy.setCurrentValueOne(0L);
            return;
        }
        Long factorValue = factor.getFactorValue();
        strategy.setCurrentValueOneDecimal(new BigDecimal(factorValue).divide(totalRemainingPrincipal, 4, RoundingMode.HALF_UP));
        MetricCompute_2Context calculateCtx = new MetricCompute_2Context();
        calculateCtx.setTotalRemainingPrincipal(totalRemainingPrincipal.toString());
        calculateCtx.setFactorValue(factorValue);
        strategy.setQuickContext(JSON.toJSONString(calculateCtx));
    }

    @Data
    static class MetricCompute_2Context {
        private String totalRemainingPrincipal;
        private Long factorValue;
    }
}
