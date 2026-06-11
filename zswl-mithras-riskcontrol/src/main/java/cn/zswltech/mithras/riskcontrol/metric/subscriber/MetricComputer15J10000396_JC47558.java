package cn.zswltech.mithras.riskcontrol.metric.subscriber;

import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.customer.application.riskcontrol.ClientProvinceQueryService;
import cn.zswltech.mithras.dto.riskcontrol.ClientDetail;
import cn.zswltech.mithras.riskcontrol.common.RiskControlIndustryClassify;
import cn.zswltech.mithras.customer.mapper.lib.client.CorpCommerceInfoLibMapper;
import cn.zswltech.mithras.customer.mapper.model.client.ClientBaseModel;
import cn.zswltech.mithras.riskcontrol.exposure.RemainingPrincipalService;
import cn.zswltech.mithras.riskcontrol.strategy.RiskControlStrategy;
import cn.zswltech.mithras.system.user.Id2NameService;
import cn.zswltech.mithras.riskcontrol.metric.AbstractMetricComputer;
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
import java.util.*;
import java.util.stream.Collectors;


/**
 * 1.客户注册地址省份不为「浙江省」
 * 2.风控行业分类不为（公用事业类、民生消费类、旅游行业、集团协同业务）
 * R=满足以上条件的合同内「未核销完毕本金之和」/满足以上条件「客户数量」
 *
 * @author zhaozhengkang
 * @deprecated 何栋要求删除（逻辑代码先保留）
 */
@Deprecated
//@Component
@Slf4j
public class MetricComputer15J10000396_JC47558 extends AbstractMetricComputer implements SubscribeSupporter<MetricComputeEvent> {
    @Resource
    private CorpCommerceInfoLibMapper corpCommerceInfoLibMapper;
    @Resource
    private RemainingPrincipalService remainingPrincipalService;
    @Resource
    private ClientProvinceQueryService clientProvinceQueryService;
    @Resource
    private Id2NameService id2NameService;


    @Override
    public String getMetricCode() {
        return "J10000396_JC47558";
    }

    @AllowConcurrentEvents
    @Subscribe
    @Override
    public void onSubscribe(MetricComputeEvent metricComputeEvent) {
        log.info("MetricComputeJ10000396_JC47558 onSubscribe");
        compute(metricComputeEvent);
    }

    @Override
    public void calculate(MetricComputeEvent event, RiskControlStrategy strategy) {
        // 查询不为浙江的客户id
        Set<Long> clientsNotInZhejiang = clientProvinceQueryService.getNotInSpecifyProvinceClientIds(Collections.singletonList("330000"));
        // 从企业商务信息表查询最新版本的风控行业分类不为（公用事业类、民生消费类、集团协同业务）的客户id
        Set<Long> targetClients = new HashSet<>();
        if (ObjectUtil.isNotEmpty(clientsNotInZhejiang)) {
            CorpCommerceInfoLibDto dto = new CorpCommerceInfoLibDto();
            dto.setInClientIds(clientsNotInZhejiang);
            dto.setNotInRiskControlIndustryClassify(Arrays.asList(
                    RiskControlIndustryClassify.PUBLIC_UTILITIES.name(),
                    RiskControlIndustryClassify.CIVIL_CONSUMPTION.name(),
                    RiskControlIndustryClassify.TRAVEL.name(),
                    RiskControlIndustryClassify.INTRA_GROUP_COLLABORATION.name()));
            targetClients = corpCommerceInfoLibMapper.listNewestCommerceInfo(dto).stream()
                    .map(ClientBaseModel::getClientId).collect(Collectors.toSet());
        }
        RemainingPrincipalQueryDto queryDto = new RemainingPrincipalQueryDto();
        queryDto.setClientIds(targetClients);
        queryDto.setEndDate(event.getSnapshotDate());
        Map<Long, Long> clientIdToRemainingPrincipal =
                remainingPrincipalService.remainingPrincipalGroupByClientId(queryDto);
        BigDecimal totalRemainingPrincipal = clientIdToRemainingPrincipal.values().stream()
                .map(BigDecimal::new)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        // 计算新值结果
        if (clientIdToRemainingPrincipal.size() == 0) {
            strategy.setCurrentValueOne(0L);
        } else {
            BigDecimal resDecimal = totalRemainingPrincipal.divide(new BigDecimal(clientIdToRemainingPrincipal.size()), 0, RoundingMode.HALF_UP);
            strategy.setCurrentValueOneDecimal(resDecimal);
            Map<Long, ClientDetail> clientDetailMap = getClientDetailMap(clientIdToRemainingPrincipal);
            strategy.setClientDetail(JSON.toJSONString(clientDetailMap.values()));
        }
    }

    private Map<Long, ClientDetail> getClientDetailMap(Map<Long, Long> clientIdToRemaining) {
        Set<Long> clientIds = new HashSet<>(clientIdToRemaining.keySet());
        Map<Long, String> clientId2Name = id2NameService.clientId2Name(clientIds);

        Map<Long, ClientDetail> clientId2Detail = new HashMap<>();
        clientIdToRemaining.forEach((clientId, remaining) -> {
            ClientDetail detail = new ClientDetail();
            detail.setClientId(clientId);
            detail.setClientName(clientId2Name.get(clientId));
            detail.setRemainingPrincipal(remaining / 10000);
            clientId2Detail.put(clientId, detail);
        });
        return clientId2Detail;
    }

    @Data
    static class MetricCompute_15Context {
        private Map<Long, Long> clientIdToRemainingPrincipal;
    }
}
