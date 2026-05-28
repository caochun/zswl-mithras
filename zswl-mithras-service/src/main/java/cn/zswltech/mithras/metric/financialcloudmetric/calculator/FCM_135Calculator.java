package cn.zswltech.mithras.metric.financialcloudmetric.calculator;

import cn.hutool.core.text.CharSequenceUtil;
import cn.zswltech.mithras.service.enums.assetclassify.AssetClassifyResultEnum;
import cn.zswltech.mithras.service.mapper.model.assetclassify.AssetClassify;
import cn.zswltech.mithras.service.mapper.model.assetclassify.AssetClassifyClient;
import cn.zswltech.mithras.service.service.assetclassify.AssetClassifyService;
import cn.zswltech.mithras.service.service.lib.assetclassify.AssetClassifyClientAuxiliaryLibService;
import cn.zswltech.mithras.service.service.riskcontrol.RemainingPrincipalServiceImpl;
import cn.zswltech.mithras.service.service.riskcontrol.dto.RemainingPrincipalQueryDto;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @description: 不良资产率 %
 * @author: zhaozhengkang
 * @date: 2023/4/13 09:57
 */
@Component
public class FCM_135Calculator implements FinancialCloudMetricCalculator {
    @Resource
    private AssetClassifyService assetClassifyService;
    @Resource
    private RemainingPrincipalServiceImpl remainingPrincipalServiceImpl;
    @Resource
    private AssetClassifyClientAuxiliaryLibService assetClassifyClientAuxiliaryLibService;

    @Override
    public String metricCode() {
        return "FCM_135";
    }

    @Override
    public BigDecimal calculate(LocalDate dateTime) {
        Optional<AssetClassify> assetClassify = assetClassifyService.currentClassify(dateTime);
        if (!assetClassify.isPresent()) {
            return BigDecimal.ZERO;
        }
        //剩余本金之和
        BigDecimal remainingPrincipal = remainingPrincipalServiceImpl.remainingPrincipal(dateTime);
        // 后三类剩余本金之和
        Set<Long> clientIds = assetClassifyClientAuxiliaryLibService
                .lastThreeNewestClassifyClientLib(assetClassify.get().getId())
                .stream().filter(assetClassifyClient -> CharSequenceUtil.equalsAny(assetClassifyClient.getClassifyResult(),
                        AssetClassifyResultEnum.LOSS.name(), AssetClassifyResultEnum.SUSPICIOUS.name(), AssetClassifyResultEnum.SECONDARY.name()))
                .map(AssetClassifyClient::getClientId).collect(Collectors.toSet());
        RemainingPrincipalQueryDto dto = new RemainingPrincipalQueryDto();
        dto.setClientIds(clientIds);
        dto.setEndDate(dateTime.with(TemporalAdjusters.lastDayOfMonth()));
        Map<Long, Long> longLongMap = remainingPrincipalServiceImpl.remainingPrincipalGroupByClientId(dto);
        BigDecimal assetClassifyRemainingPrincipal = longLongMap.values().stream().map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add);
        // 计算
        return assetClassifyRemainingPrincipal
                .multiply(BigDecimal.valueOf(1000000))
                .divide(remainingPrincipal, 4, RoundingMode.HALF_UP);
    }
}
