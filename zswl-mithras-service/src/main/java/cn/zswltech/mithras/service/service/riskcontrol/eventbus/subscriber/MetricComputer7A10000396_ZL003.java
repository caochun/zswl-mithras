package cn.zswltech.mithras.service.service.riskcontrol.eventbus.subscriber;

import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.metric.mapper.model.RiskMetricFactor;
import cn.zswltech.mithras.metric.service.RiskMetricFactorService;
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
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
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
 * R=按集团维度（集团注册地址为「浙江省」或风控行业分类为「集团协同业务」）的剩余本金余额/资产负债表「所有者权益（或股东权益）合计@期末余额」
 * 风险策略调整，修改后的逻辑：
 * R=按集团维度（风控行业分类为「集团协同业务」）的剩余本金之和/资产负债表「所有者权益（或股东权益）合计@期末余额」
 *
 * @author zhaozhengkang
 */
@Component
@Slf4j
public class MetricComputer7A10000396_ZL003 extends AbstractMetricComputer implements SubscribeSupporter<MetricComputeEvent> {
    public static final String FACTOR_NAME = "所有者权益（或股东权益）合计@期末余额";
    public static final String FACTOR_TABLE = "资产负债表";
    @Resource
    private CorpCommerceInfoLibMapper corpCommerceInfoLibMapper;
    @Resource
    private RemainingPrincipalServiceImpl remainingPrincipalServiceImpl;
    @Resource
    private RiskMetricFactorService factorService;
    @Resource
    private ClientService clientService;

    @Override
    public String getMetricCode() {
        return "A10000396_ZL003";
    }

    @AllowConcurrentEvents
    @Subscribe
    @Override
    public void onSubscribe(MetricComputeEvent metricComputeEvent) {
        log.info("MetricComputeA10000396_ZL003 onSubscribe");
        compute(metricComputeEvent);
    }

    @Override
    public void calculate(MetricComputeEvent event, RiskControlStrategy strategy) {
        long maxRemainingPrincipal = 0L;
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
//        // 从企业地址表查询最新版本的浙江省的客户id
//        Set<Long> clientsInZhejiang = clientService.getSpecifyProvinceClientIds(Collections.singletonList("330000"));
//        // 获取浙江的客户lib
//        CorpCommerceInfoLibDto dto = new CorpCommerceInfoLibDto();
//        dto.setInClientIds(clientsInZhejiang);
//        List<CorpCommerceInfoLib> zhejiangClients = corpCommerceInfoLibMapper.listNewestCommerceInfo(dto);
        // 获取集团协同业务的客户
        List<CorpCommerceInfoLib> targetClients = corpCommerceInfoLibMapper.intraGroupClients();
//        // 或-> 取并集
//        if (ObjectUtil.isNotEmpty(zhejiangClients)) {
//            targetClients.addAll(zhejiangClients);
//        }

        Map<Long, Long> client2Group = targetClients.stream()
                .filter(lib ->
                        lib.getBelongGroupClientId() != null && lib.getBelongGroupClientId() != -1L)
                .collect(Collectors.toMap(CorpCommerceInfoLib::getClientId,
                        CorpCommerceInfoLib::getBelongGroupClientId, (a, b) -> a));
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
        // 计算新值结果保留两位小数
        BigDecimal res = new BigDecimal(maxRemainingPrincipal).divide(new BigDecimal(factor.getFactorValue()), 4, RoundingMode.HALF_UP).multiply(new BigDecimal(10000));
        strategy.setCurrentValueOne(res.longValue());
        MetricCompute_7Context calculateCtx = new MetricCompute_7Context();
        calculateCtx.setGroupIdToRemainingPrincipal(groupIdToRemainingPrincipal);
        calculateCtx.setMaxRemainingPrincipal(maxRemainingPrincipal);
        calculateCtx.setFactorValue(factor.getFactorValue());
        strategy.setQuickContext(JSON.toJSONString(calculateCtx));
    }

    @Data
    static class MetricCompute_7Context {
        private Long maxRemainingPrincipal;
        private Map<Long, Long> groupIdToRemainingPrincipal;
        private Long factorValue;
    }
}

