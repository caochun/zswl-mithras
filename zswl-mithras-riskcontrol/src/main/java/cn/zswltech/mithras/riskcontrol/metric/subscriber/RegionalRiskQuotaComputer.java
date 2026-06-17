package cn.zswltech.mithras.riskcontrol.metric.subscriber;

import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.dto.riskcontrol.ClientDetail;
import cn.zswltech.mithras.foundation.enums.common.RiskControlIndustryClassify;
import cn.zswltech.mithras.riskcontrol.strategy.RiskControlStrategy;
import cn.zswltech.mithras.foundation.port.ClientNameResolver;
import cn.zswltech.mithras.riskcontrol.application.port.RiskControlClientFactPort;
import cn.zswltech.mithras.riskcontrol.application.port.RiskControlProjectReviewFactPort;
import cn.zswltech.mithras.riskcontrol.metric.AbstractMetricComputer;
import cn.zswltech.mithras.riskcontrol.exposure.RemainingPrincipalService;
import cn.zswltech.mithras.riskcontrol.common.RegionalProjectClassify;
import cn.zswltech.mithras.riskcontrol.exposure.RemainingPrincipalQueryDto;
import cn.zswltech.mithras.riskcontrol.metric.MetricComputeEvent;
import com.alibaba.fastjson.JSON;
import lombok.Data;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @description: 区域风险指标抽象计算类
 * @author: zhaozhengkang
 * @date: 2023/2/14 16:43
 */
public abstract class RegionalRiskQuotaComputer extends AbstractMetricComputer {
    @Resource
    private RemainingPrincipalService remainingPrincipalServiceImpl;
    @Resource
    private RiskControlClientFactPort clientFactPort;
    @Resource
    private RiskControlProjectReviewFactPort projectReviewFactPort;
    @Resource
    private ClientNameResolver clientNameResolver;

    protected abstract List<RegionalProjectClassify> getRegionalProjectClassify();

    @Override
    protected void calculate(MetricComputeEvent event, RiskControlStrategy strategy) {
        Set<Long> clientIds = clientFactPort.clientIdsNotInRiskControlIndustryClassify(
                new HashSet<>(Arrays.asList(RiskControlIndustryClassify.PUBLIC_UTILITIES.name(),
                        RiskControlIndustryClassify.CIVIL_CONSUMPTION.name(),
                        //RiskControlIndustryClassify.TRAVEL.name(),
                        RiskControlIndustryClassify.INTRA_GROUP_COLLABORATION.name())));
        Set<Long> contractIds = projectReviewFactPort.newestContractIdsByClientIdsAndRegionalClassifies(
                clientIds,
                getRegionalProjectClassify().stream().map(Enum::name).collect(Collectors.toList()),
                event.getSnapshotDate());
        Map<Long, Long> clientIdToRemaining = remainingPrincipalServiceImpl
                .remainingPrincipalGroupByClientId(new RemainingPrincipalQueryDto()
                        .setContractIds(contractIds)
                        .setEndDate(event.getSnapshotDate()));
        // 剩余本金
        BigDecimal totalRemainingPrincipal = clientIdToRemaining.values().stream()
                .map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add);
        // 保证金余额
        Map<Long, BigDecimal> deposits =
                remainingPrincipalServiceImpl.totalDepositGroupByClientIds(clientIdToRemaining.keySet());
        BigDecimal totalDeposit = deposits.values().stream().reduce(BigDecimal.ZERO, BigDecimal::add);

        strategy.setCurrentValueOneDecimal(totalRemainingPrincipal.subtract(totalDeposit));
        RegionlRiskQuotaComputerContext calculateCtx = new RegionlRiskQuotaComputerContext();
        calculateCtx.setTotalRemainingPrincipal(totalRemainingPrincipal.toString());
        calculateCtx.setTotalDeposit(totalDeposit.toString());
        strategy.setQuickContext(JSON.toJSONString(calculateCtx));

        Map<Long, ClientDetail> regionalClassifyClientDetail = regionalClassifyClientDetail(clientIdToRemaining, deposits);
        strategy.setClientDetail(JSON.toJSONString(regionalClassifyClientDetail.values()));
    }

    protected Map<Long, ClientDetail> regionalClassifyClientDetail(
            Map<Long, Long> clientIdToRemaining, Map<Long, BigDecimal> clientIdToDeposit) {
        Set<Long> clientIds = new HashSet<>(clientIdToRemaining.keySet());
        if (clientIdToDeposit != null) {
            clientIds.addAll(clientIdToDeposit.keySet());
        }
        Map<Long, String> clientId2Name = clientNameResolver.clientId2Name(clientIds);
        // 生成客户明细数据
        Map<Long, ClientDetail> clientId2Detail = new HashMap<>();
        clientIdToRemaining.forEach((clientId, remaining) -> {
            ClientDetail detail = new ClientDetail();
            detail.setClientId(clientId);
            detail.setClientName(clientId2Name.get(clientId));
            detail.setRemainingPrincipal(remaining / 10000);
            if (clientIdToDeposit.containsKey(clientId)) {
                detail.setDeposit(clientIdToDeposit.get(clientId).longValue() / 10000);
                clientIdToDeposit.remove(clientId);
            } else {
                detail.setDeposit(0L);
            }
            clientId2Detail.put(clientId, detail);
        });
        if (ObjectUtil.isNotEmpty(clientIdToDeposit)) {
            clientIdToDeposit.forEach((clientId, deposit) -> {
                ClientDetail detail = new ClientDetail();
                detail.setClientId(clientId);
                detail.setClientName(clientId2Name.get(clientId));
                detail.setDeposit(deposit.longValue() / 10000);
                clientId2Detail.put(clientId, detail);
            });
        }
        return clientId2Detail;
    }

    @Data
    static class RegionlRiskQuotaComputerContext {
        private String totalRemainingPrincipal;
        private String totalDeposit;
    }
}
