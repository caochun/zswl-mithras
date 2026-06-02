package cn.zswltech.mithras.service.service.riskcontrol.eventbus.subscriber;

import cn.zswltech.mithras.service.mapper.lib.client.CorpCommerceInfoLibMapper;
import cn.zswltech.mithras.service.mapper.model.client.CorpCommerceInfoLib;
import cn.zswltech.mithras.service.mapper.model.riskcontrol.RiskControlStrategy;
import cn.zswltech.mithras.service.service.client.ClientService;
import cn.zswltech.mithras.service.service.riskcontrol.AbstractMetricComputer;
import cn.zswltech.mithras.service.service.riskcontrol.RemainingPrincipalServiceImpl;
import cn.zswltech.mithras.service.service.riskcontrol.dto.CorpCommerceInfoLibDto;
import cn.zswltech.mithras.service.service.riskcontrol.dto.RemainingPrincipalQueryDto;
import cn.zswltech.mithras.riskcontrol.eventbus.MetricComputeEvent;
import cn.zswltech.mithras.riskcontrol.eventbus.SubscribeSupporter;
import com.alibaba.fastjson.JSON;
import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * R=按单个客户维度（客户注册地址为「浙江省」的剩余本金之和）
 * @author dingqi
 * @date 2024/11/26
 * @description
 */
@Slf4j
@Component
public class MetricComputer37A10000396_MD002 extends AbstractMetricComputer implements SubscribeSupporter<MetricComputeEvent> {
    @Resource
    private CorpCommerceInfoLibMapper corpCommerceInfoLibMapper;
    @Resource
    private RemainingPrincipalServiceImpl remainingPrincipalServiceImpl;
    @Resource
    private ClientService clientService;

    @Override
    protected String getMetricCode() {
        return "A10000396_MD002";
    }

    @Override
    protected void calculate(MetricComputeEvent event, RiskControlStrategy strategy) {
        Long maxRemainingPrincipal = 0L;
        // 从企业地址表查询最新版本的浙江省的客户id
        Set<Long> clientsInZhejiang = clientService.getSpecifyProvinceClientIds(Collections.singletonList("330000"));
        CorpCommerceInfoLibDto dto = new CorpCommerceInfoLibDto();
        dto.setInClientIds(clientsInZhejiang);
        Set<Long> targetClientIds = corpCommerceInfoLibMapper.listNewestCommerceInfo(dto).stream().map(CorpCommerceInfoLib::getClientId).collect(Collectors.toSet());
        RemainingPrincipalQueryDto queryDto = new RemainingPrincipalQueryDto();
        queryDto.setClientIds(targetClientIds);
        queryDto.setEndDate(event.getSnapshotDate());
        Map<Long, Long> clientIdToRemainingPrincipal =
                remainingPrincipalServiceImpl.remainingPrincipalGroupByClientId(queryDto);
        for (Long remainingPrincipal : clientIdToRemainingPrincipal.values()) {
            maxRemainingPrincipal = Math.max(maxRemainingPrincipal, remainingPrincipal);
        }
        strategy.setCurrentValueOneDecimal(new BigDecimal(maxRemainingPrincipal));
        MetricComputer37A10000396_MD002.MetricCompute_37Context calculateCtx = new MetricComputer37A10000396_MD002.MetricCompute_37Context();
        calculateCtx.setClientIdToRemainingPrincipal(clientIdToRemainingPrincipal);
        calculateCtx.setMaxRemainingPrincipal(maxRemainingPrincipal);
        strategy.setQuickContext(JSON.toJSONString(calculateCtx));
    }

    @AllowConcurrentEvents
    @Subscribe
    @Override
    public void onSubscribe(MetricComputeEvent event) {
        log.info("MetricComputeA10000396_MD002 onSubscribe");
        compute(event);
    }

    @Data
    static class MetricCompute_37Context {
        private Map<Long, Long> clientIdToRemainingPrincipal;
        private Long maxRemainingPrincipal;
    }
}
