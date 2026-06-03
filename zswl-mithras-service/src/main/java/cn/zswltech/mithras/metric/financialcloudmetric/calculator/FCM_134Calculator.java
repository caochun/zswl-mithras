package cn.zswltech.mithras.metric.financialcloudmetric.calculator;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Pair;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.zswltech.mithras.service.enums.CashFlowItemEnum;
import cn.zswltech.mithras.assetclassify.domain.enums.AssetClassifyResultEnum;
import cn.zswltech.mithras.assetclassify.infrastructure.persistence.mapper.lib.AssetClassifyClientAuxiliaryLibMapper;
import cn.zswltech.mithras.assetclassify.infrastructure.persistence.mapper.model.AssetClassify;
import cn.zswltech.mithras.assetclassify.infrastructure.persistence.mapper.model.AssetClassifyClient;
import cn.zswltech.mithras.collection.mapper.model.CollectionBaseInfo;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.service.service.assetclassify.AssetClassifyService;
import cn.zswltech.mithras.service.service.collection.CollectionBaseInfoService;
import cn.zswltech.mithras.service.service.contract.ContractBaseInfoService;
import cn.zswltech.mithras.service.service.riskcontrol.RemainingPrincipalServiceImpl;
import cn.zswltech.mithras.riskcontrol.exposure.RemainingPrincipalQueryDto;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.apache.commons.lang3.tuple.Triple;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @description: 不良资产金额 (万元)
 * @author: zhaozhengkang
 * @date: 2023/4/13 09:57
 */
@Component
public class FCM_134Calculator implements FinancialCloudMetricCalculator {

    @Resource
    private RemainingPrincipalServiceImpl remainingPrincipalServiceImpl;
    @Resource
    private AssetClassifyService assetClassifyService;
    @Resource
    private AssetClassifyClientAuxiliaryLibMapper assetClassifyClientAuxiliaryLibMapper;

    @Override
    public String metricCode() {
        return "FCM_134";
    }

    @Override
    public BigDecimal calculate(LocalDate dateTime) {
        Optional<AssetClassify> assetClassify = assetClassifyService.currentClassify(dateTime.with(TemporalAdjusters.lastDayOfMonth()));
        if (!assetClassify.isPresent()) {
            return BigDecimal.ZERO;
        }
        // 后三类剩余本金之和 ?
        Set<Long> clientIds = assetClassifyClientAuxiliaryLibMapper
                .newestClassifyClientLib(assetClassify.get().getId())
                .stream().filter(assetClassifyClient -> CharSequenceUtil.equalsAny(assetClassifyClient.getClassifyResult(),
                        AssetClassifyResultEnum.LOSS.name(), AssetClassifyResultEnum.SUSPICIOUS.name(), AssetClassifyResultEnum.SECONDARY.name()))
                .map(AssetClassifyClient::getClientId).collect(Collectors.toSet());
        RemainingPrincipalQueryDto dto = new RemainingPrincipalQueryDto();
        dto.setClientIds(clientIds);
        dto.setEndDate(dateTime.with(TemporalAdjusters.lastDayOfMonth()));
        Map<Long, Long> longLongMap = remainingPrincipalServiceImpl.remainingPrincipalGroupByClientId(dto);
        return longLongMap.values().stream().map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
