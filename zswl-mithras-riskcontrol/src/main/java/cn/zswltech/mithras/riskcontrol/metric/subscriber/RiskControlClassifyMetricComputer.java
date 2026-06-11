package cn.zswltech.mithras.riskcontrol.metric.subscriber;

import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.dto.riskcontrol.ClientDetail;
import cn.zswltech.mithras.customer.mapper.lib.client.CorpCommerceInfoLibMapper;
import cn.zswltech.mithras.customer.model.client.ClientBaseModel;
import cn.zswltech.mithras.riskcontrol.strategy.RiskControlStrategy;
import cn.zswltech.mithras.system.user.Id2NameService;
import cn.zswltech.mithras.riskcontrol.metric.AbstractMetricComputer;
import cn.zswltech.mithras.riskcontrol.exposure.RemainingPrincipalService;
import cn.zswltech.mithras.customer.versioning.dto.CorpCommerceInfoLibDto;
import cn.zswltech.mithras.riskcontrol.exposure.RemainingPrincipalQueryDto;
import cn.zswltech.mithras.riskcontrol.metric.MetricComputeEvent;
import com.alibaba.fastjson.JSON;
import lombok.Data;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @description: 风控行业分类的抽象计算类
 * @author: zhaozhengkang
 * @date: 2023/2/14 14:51
 */
public abstract class RiskControlClassifyMetricComputer extends AbstractMetricComputer {

    public static final List<String> RELATED_METRIC_CODES =
            Arrays.asList("A10000396_JC033", "A10000396_JC034", "A10000396_JC035", "A10000396_JC036", "J10000396_JC47568",
                    "A10000396_JC038", "A10000396_JC039", "A10000396_JC040", "J10000396_JC47578", "J10000396_JC47588",
                    "J10000396_FJC47608", "A10000396_ZL039", "J10000396_JC47598");

    @Resource
    private CorpCommerceInfoLibMapper corpCommerceInfoLibMapper;
    @Resource
    private RemainingPrincipalService remainingPrincipalServiceImpl;
    @Resource
    private Id2NameService id2NameService;

    public abstract List<String> getIndustryClassify();

    @Override
    protected void calculate(MetricComputeEvent event, RiskControlStrategy strategy) {
        CorpCommerceInfoLibDto commerceDto = new CorpCommerceInfoLibDto();
        commerceDto.setInRiskControlIndustryClassify(getIndustryClassify());
        Set<Long> targetClients = corpCommerceInfoLibMapper.listNewestCommerceInfo(commerceDto)
                .stream().map(ClientBaseModel::getClientId).collect(Collectors.toSet());
        RemainingPrincipalQueryDto dto = new RemainingPrincipalQueryDto();
        dto.setClientIds(targetClients);
        dto.setEndDate(event.getSnapshotDate());
        Map<Long, Long> clientIdToRemaining = remainingPrincipalServiceImpl.remainingPrincipalGroupByClientId(dto);
        //保证金余额
        Map<Long, BigDecimal> deposits
                = remainingPrincipalServiceImpl.totalDepositGroupByClientIds(targetClients);
        BigDecimal totalDeposit = deposits.values().stream().reduce(BigDecimal.ZERO, BigDecimal::add);
        // 剩余本金之和
        BigDecimal totalRemainingPrincipal = clientIdToRemaining.values().stream()
                .map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add);

        RiskControlClassifyMetricComputerContext calculateCtx = new RiskControlClassifyMetricComputerContext();
        calculateCtx.setTotalRemainingPrincipal(totalRemainingPrincipal.toString());
        calculateCtx.setTotalDeposit(totalDeposit.toString());

        // 生成客户明细数据
        Map<Long, ClientDetail> clientDetailMap = industryClassifyClientDetail(clientIdToRemaining, deposits);
        strategy.setClientDetail(JSON.toJSONString(clientDetailMap.values()));
        strategy.setQuickContext(JSON.toJSONString(calculateCtx));
        strategy.setCurrentValueOneDecimal(totalRemainingPrincipal.subtract(totalDeposit));
    }

    @Data
    static class RiskControlClassifyMetricComputerContext {
        private String totalRemainingPrincipal;
        private String totalDeposit;
    }

    protected Map<Long, ClientDetail> industryClassifyClientDetail(
            Map<Long, Long> clientIdToRemaining, Map<Long, BigDecimal> clientIdToDeposit) {
        Set<Long> clientIds = new HashSet<>(clientIdToRemaining.keySet());
        if (clientIdToDeposit != null) {
            clientIds.addAll(clientIdToDeposit.keySet());
        }
        Map<Long, String> clientId2Name = id2NameService.clientId2Name(clientIds);
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
}
