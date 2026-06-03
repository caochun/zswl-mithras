package cn.zswltech.mithras.service.service.riskcontrol.eventbus.subscriber;

import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.lib.client.CorpCommerceInfoLibMapper;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.model.client.CorpCommerceInfoLib;
import cn.zswltech.mithras.riskcontrol.strategy.RiskControlStrategy;
import cn.zswltech.mithras.service.service.client.ClientService;
import cn.zswltech.mithras.service.service.riskcontrol.AbstractMetricComputer;
import cn.zswltech.mithras.service.service.riskcontrol.RemainingPrincipalServiceImpl;
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
import java.util.*;

/**
 * R=按集团维度（客户注册地址为「浙江省」）的剩余本金之和
 * @author dingqi
 * @date 2024/11/26
 * @description
 */
@Slf4j
@Component
public class MetricComputer38A10000396_MD003 extends AbstractMetricComputer implements SubscribeSupporter<MetricComputeEvent> {
    @Resource
    private CorpCommerceInfoLibMapper corpCommerceInfoLibMapper;
    @Resource
    private RemainingPrincipalServiceImpl remainingPrincipalServiceImpl;
    @Resource
    private ClientService clientService;

    @Override
    protected String getMetricCode() {
        return "A10000396_MD003";
    }

    @Override
    protected void calculate(MetricComputeEvent event, RiskControlStrategy strategy) {
        long maxRemainingPrincipal = 0L;
        // 从企业地址表查询最新版本的浙江省的客户id
        Set<Long> clientsInZhejiang = clientService.getSpecifyProvinceClientIds(Collections.singletonList("330000"));
        // 从企业商务信息表查询最新版本的浙江省的客户id
        CorpCommerceInfoLibDto dto = new CorpCommerceInfoLibDto();
        dto.setInClientIds(clientsInZhejiang);
        List<CorpCommerceInfoLib> corpCommerceInfoLibs = corpCommerceInfoLibMapper.listNewestCommerceInfo(dto);
        Map<Long, Long> client2Group = new HashMap<>();
        for (CorpCommerceInfoLib lib : corpCommerceInfoLibs) {
            if (lib.getBelongGroupClientId() != null && lib.getBelongGroupClientId() != -1L) {
                client2Group.put(lib.getClientId(), lib.getBelongGroupClientId());
            }
        }
        RemainingPrincipalQueryDto queryDto = new RemainingPrincipalQueryDto();
        queryDto.setClientIds(client2Group.keySet());
        queryDto.setEndDate(event.getSnapshotDate());
        Map<Long, Long> clientIdToRemainingPrincipal =
                remainingPrincipalServiceImpl.remainingPrincipalGroupByClientId(queryDto);
        Map<Long, Long> groupIdToRemainingPrincipal = new HashMap<>();
        for (Map.Entry<Long, Long> entry : clientIdToRemainingPrincipal.entrySet()) {
            Long groupId = client2Group.get(entry.getKey());
            groupIdToRemainingPrincipal.put(groupId,
                    groupIdToRemainingPrincipal.getOrDefault(groupId, 0L) + entry.getValue());
            maxRemainingPrincipal = Math.max(maxRemainingPrincipal, groupIdToRemainingPrincipal.get(groupId));
        }
        strategy.setCurrentValueOneDecimal(new BigDecimal(maxRemainingPrincipal));
        MetricComputer38A10000396_MD003.MetricCompute_38Context calculateCtx = new MetricComputer38A10000396_MD003.MetricCompute_38Context();
        calculateCtx.setGroupIdToRemainingPrincipal(groupIdToRemainingPrincipal);
        calculateCtx.setMaxRemainingPrincipal(maxRemainingPrincipal);
        strategy.setQuickContext(JSON.toJSONString(calculateCtx));
    }

    @AllowConcurrentEvents
    @Subscribe
    @Override
    public void onSubscribe(MetricComputeEvent event) {
        log.info("MetricComputeA10000396_MD003 onSubscribe");
        compute(event);
    }

    @Data
    static class MetricCompute_38Context {
        private Long maxRemainingPrincipal;
        private Map<Long, Long> groupIdToRemainingPrincipal;
    }
}
