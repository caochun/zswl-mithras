package cn.zswltech.mithras.metric.financialcloudmetric.calculator;

import cn.zswltech.mithras.assetclassify.mapper.model.AssetClassify;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * @description: 不良资产金额 (万元)
 * @author: zhaozhengkang
 * @date: 2023/4/13 09:57
 */
@Component
public class FCM_134Calculator implements FinancialCloudMetricCalculator {

    @Resource
    private ContractRemainingPrincipalReader contractRemainingPrincipalReader;
    @Resource
    private AssetClassifyMetricReader assetClassifyMetricReader;

    @Override
    public String metricCode() {
        return "FCM_134";
    }

    @Override
    public BigDecimal calculate(LocalDate dateTime) {
        Optional<AssetClassify> assetClassify = assetClassifyMetricReader.currentClassify(dateTime.with(TemporalAdjusters.lastDayOfMonth()));
        if (!assetClassify.isPresent()) {
            return BigDecimal.ZERO;
        }
        // 后三类剩余本金之和 ?
        Set<Long> clientIds = assetClassifyMetricReader.listAdverseClientIds(assetClassify.get().getId());
        Map<Long, Long> longLongMap = contractRemainingPrincipalReader.remainingPrincipalGroupByClientId(clientIds, dateTime.with(TemporalAdjusters.lastDayOfMonth()));
        return longLongMap.values().stream().map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
