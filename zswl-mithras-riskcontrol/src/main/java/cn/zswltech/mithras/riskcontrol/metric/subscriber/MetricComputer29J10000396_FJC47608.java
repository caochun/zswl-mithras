package cn.zswltech.mithras.riskcontrol.metric.subscriber;

import cn.zswltech.mithras.foundation.enums.common.RiskControlIndustryClassify;
import cn.zswltech.mithras.riskcontrol.strategy.RiskControlStrategy;
import cn.zswltech.mithras.riskcontrol.application.port.RiskControlClientFactPort;
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
import java.util.*;


/**
 * 1.风控行业分类为「创新业务，即新增行业板块」
 * R=满足以上条件的「未核销本金之和」
 *
 * @author zhaozhengkang
 */
@Component
@Slf4j
public class MetricComputer29J10000396_FJC47608 extends AbstractMetricComputer
        implements SubscribeSupporter<MetricComputeEvent> {
    @Resource
    private RiskControlClientFactPort clientFactPort;
    @Resource
    private RemainingPrincipalService remainingPrincipalServiceImpl;

    @Override
    public String getMetricCode() {
        return "J10000396_FJC47608";
    }

    @AllowConcurrentEvents
    @Subscribe
    @Override
    public void onSubscribe(MetricComputeEvent metricComputeEvent) {
        log.info("MetricComputeJ10000396_FJC47608 onSubscribe");
        compute(metricComputeEvent);
    }

    @Override
    protected void calculate(MetricComputeEvent event, RiskControlStrategy strategy) {
        Map<Long, String> industryTypeMap = clientFactPort.clientIdToIndustryTypeInRiskControlIndustryClassify(
                Collections.singleton(RiskControlIndustryClassify.INNOVATION_BUSINESS.name()));
        Set<Long> targetClients = industryTypeMap.keySet();
        RemainingPrincipalQueryDto dto = new RemainingPrincipalQueryDto();
        dto.setClientIds(targetClients);
        dto.setEndDate(event.getSnapshotDate());
        Map<Long, Long> clientIdToRemaining = remainingPrincipalServiceImpl.remainingPrincipalGroupByClientId(dto);
        Map<Long, Long> clientIdToDeposit = remainingPrincipalServiceImpl.depositGroupByClientId(clientIdToRemaining.keySet());

        Map<String, BigDecimal> industryToRemaining = new HashMap<>();

        BigDecimal maxRemaining = BigDecimal.ZERO;
        for (Map.Entry<Long, Long> entry : clientIdToRemaining.entrySet()) {
            Long clientId = entry.getKey();
            String industry = industryTypeMap.getOrDefault(clientId, "NONE");
            String twoLevel = industry.substring(0, 3);
            BigDecimal oneRemaining = industryToRemaining.getOrDefault(twoLevel, BigDecimal.ZERO);
            BigDecimal afterAdd = oneRemaining.add(new BigDecimal(clientIdToRemaining.get(clientId) - clientIdToDeposit.getOrDefault(clientId, 0L)));
            industryToRemaining.put(twoLevel, afterAdd);
            if (afterAdd.compareTo(maxRemaining) > 0) {
                maxRemaining = afterAdd;
            }
        }
        if (maxRemaining.compareTo(BigDecimal.ZERO) > 0) {
            strategy.setCurrentValueOneDecimal(maxRemaining);
        }

        Map<String, String> industryToRemainingStr = new HashMap<>();
        industryToRemaining.forEach((k, v) -> industryToRemainingStr.put(k, v.toString()));
        MetricComputer29ComputerContext ctx = new MetricComputer29ComputerContext();
        ctx.setTotalRemaining(industryToRemainingStr);
        ctx.setTotalRemainingPrincipal(clientIdToRemaining.values().stream().map(BigDecimal::new)
                .reduce(BigDecimal.ZERO, BigDecimal::add).toString());
        strategy.setQuickContext(JSON.toJSONString(ctx));
    }

    @Data
    public static class MetricComputer29ComputerContext {
        /**
         * 客户id map total
         */
        private Map<String, String> totalRemaining;

        /**
         * 整体剩余本金
         */
        private String totalRemainingPrincipal;
    }
}
