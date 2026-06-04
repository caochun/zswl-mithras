package cn.zswltech.mithras.metric.financialcloudmetric.calculator;

import cn.zswltech.mithras.assetclassify.infrastructure.persistence.mapper.model.AssetClassify;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * @description: 不良资产率 %
 * @author: zhaozhengkang
 * @date: 2023/4/13 09:57
 */
@Component
public class FCM_135Calculator implements FinancialCloudMetricCalculator {
    @Resource
    private AssetClassifyMetricReader assetClassifyMetricReader;
    @Resource
    private ContractRemainingPrincipalReader contractRemainingPrincipalReader;

    @Override
    public String metricCode() {
        return "FCM_135";
    }

    @Override
    public BigDecimal calculate(LocalDate dateTime) {
        Optional<AssetClassify> assetClassify = assetClassifyMetricReader.currentClassify(dateTime);
        if (!assetClassify.isPresent()) {
            return BigDecimal.ZERO;
        }
        //剩余本金之和
        BigDecimal remainingPrincipal = contractRemainingPrincipalReader.remainingPrincipal(dateTime);
        // 后三类剩余本金之和
        Set<Long> clientIds = assetClassifyMetricReader.listAdverseClientIds(assetClassify.get().getId());
        Map<Long, Long> longLongMap = contractRemainingPrincipalReader.remainingPrincipalGroupByClientId(clientIds, dateTime.with(TemporalAdjusters.lastDayOfMonth()));
        BigDecimal assetClassifyRemainingPrincipal = longLongMap.values().stream().map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add);
        // 计算
        return assetClassifyRemainingPrincipal
                .multiply(BigDecimal.valueOf(1000000))
                .divide(remainingPrincipal, 4, RoundingMode.HALF_UP);
    }
}
