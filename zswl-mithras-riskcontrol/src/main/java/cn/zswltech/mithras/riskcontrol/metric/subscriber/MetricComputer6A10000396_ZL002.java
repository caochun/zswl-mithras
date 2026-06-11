package cn.zswltech.mithras.riskcontrol.metric.subscriber;

import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.riskcontrol.metric.RiskMetricFactorQueryService;
import cn.zswltech.mithras.riskcontrol.metric.RiskMetricFactorValue;
import cn.zswltech.mithras.customer.mapper.lib.client.CorpCommerceInfoLibMapper;
import cn.zswltech.mithras.customer.mapper.model.client.ClientBaseModel;
import cn.zswltech.mithras.riskcontrol.strategy.RiskControlStrategy;
import cn.zswltech.mithras.customer.application.riskcontrol.ClientProvinceQueryService;
import cn.zswltech.mithras.riskcontrol.metric.AbstractMetricComputer;
import cn.zswltech.mithras.riskcontrol.exposure.RemainingPrincipalService;
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
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


/**
 * R=按单个客户维度（客户注册地址为「浙江省」或风控行业分类为「集团协同业务」）的剩余本金余额（/资产负债表「所有者权益（或股东权益）合计@期末余额」
 * 风险策略调整，修改后的逻辑：
 * R=按单个客户维度（风控行业分类为「集团协同业务」）的剩余本金之和/资产负债表「所有者权益（或股东权益）合计@期末余额」
 *
 * @author zhaozhengkang
 */
@Component
@Slf4j
public class MetricComputer6A10000396_ZL002 extends AbstractMetricComputer implements SubscribeSupporter<MetricComputeEvent> {
    public static final String FACTOR_NAME = "所有者权益（或股东权益）合计@期末余额";
    public static final String FACTOR_TABLE = "资产负债表";
    @Resource
    private CorpCommerceInfoLibMapper corpCommerceInfoLibMapper;
    @Resource
    private RemainingPrincipalService remainingPrincipalService;
    @Resource
    private RiskMetricFactorQueryService factorService;
    @Resource
    private ClientProvinceQueryService clientProvinceQueryService;

    @Override
    public String getMetricCode() {
        return "A10000396_ZL002";
    }

    @AllowConcurrentEvents
    @Subscribe
    @Override
    public void onSubscribe(MetricComputeEvent metricComputeEvent) {
        log.info("MetricComputeA10000396_ZL002 onSubscribe");
        compute(metricComputeEvent);
    }

    @Override
    public void calculate(MetricComputeEvent event, RiskControlStrategy strategy) {
        long maxRemainingPrincipal = 0L;
        RiskMetricFactorValue factor = factorService.newestFactor(FACTOR_NAME, FACTOR_TABLE, event.getFactorQueryDate());
        if (factor == null) {
            strategy.setNullReason("上月指标因子未导入");
            strategy.setCurrentValueOne(null);
            strategy.setCurrentValueTwo(null);
            return;
        }
//        //获取在浙江省内的客户ID
//        Set<Long> clientsInZhejiang = clientProvinceQueryService.getSpecifyProvinceClientIds(Collections.singletonList("330000"));
        // 获取风控行业分类为集团协同业务的客户ID
        Set<Long> targetClientIds = corpCommerceInfoLibMapper.intraGroupClients()
                .stream().map(ClientBaseModel::getClientId).collect(Collectors.toSet());
//        // 或->取并集
//        if (ObjectUtil.isNotEmpty(clientsInZhejiang)) {
//            targetClientIds.addAll(clientsInZhejiang);
//        }
        RemainingPrincipalQueryDto queryDto = new RemainingPrincipalQueryDto();
        queryDto.setClientIds(targetClientIds);
        queryDto.setEndDate(event.getSnapshotDate());
        Map<Long, Long> clientIdToRemainingPrincipal =
                remainingPrincipalService.remainingPrincipalGroupByClientId(queryDto);

        for (Long remainingPrincipal : clientIdToRemainingPrincipal.values()) {
            maxRemainingPrincipal = Math.max(maxRemainingPrincipal, remainingPrincipal);
        }
        // 计算新值结果保留两位小数
        BigDecimal res = new BigDecimal(maxRemainingPrincipal)
                .divide(new BigDecimal(factor.getFactorValue()), 4, RoundingMode.HALF_UP)
                .multiply(new BigDecimal(10000));

        strategy.setCurrentValueOne(res.longValue());
        MetricCompute_6Context calculateCtx = new MetricCompute_6Context();
        calculateCtx.setFactorValue(factor.getFactorValue());
        calculateCtx.setMaxRemainingPrincipal(maxRemainingPrincipal);
        calculateCtx.setClientIdToRemainingPrincipal(clientIdToRemainingPrincipal);
        strategy.setQuickContext(JSON.toJSONString(calculateCtx));
    }

    @Data
    static class MetricCompute_6Context {
        private Map<Long, Long> clientIdToRemainingPrincipal;
        private Long maxRemainingPrincipal;
        private Long factorValue;
    }
}


