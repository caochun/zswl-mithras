package cn.zswltech.mithras.service.service.riskcontrol.eventbus.subscriber;

import cn.hutool.core.lang.Pair;
import cn.zswltech.mithras.riskcontrol.strategy.RiskControlStrategy;
import cn.zswltech.mithras.service.service.riskcontrol.AbstractMetricComputer;
import cn.zswltech.mithras.service.service.riskcontrol.RemainingPrincipalServiceImpl;
import cn.zswltech.mithras.riskcontrol.metric.MetricComputeEvent;
import cn.zswltech.mithras.riskcontrol.metric.SubscribeSupporter;
import com.alibaba.fastjson.JSON;
import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Triple;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Map;


/**
 * R=剩余逾期金额（本金+利息）之和/剩余本金利息之和
 *
 * @author zhaozhengkang
 */
@Component
@Slf4j
public class MetricComputer4A10000396_JC014 extends AbstractMetricComputer implements SubscribeSupporter<MetricComputeEvent> {
    @Resource
    private RemainingPrincipalServiceImpl remainingPrincipalServiceImpl;


    @Override
    public String getMetricCode() {
        return "A10000396_JC014";
    }

    @AllowConcurrentEvents
    @Subscribe
    @Override
    public void onSubscribe(MetricComputeEvent metricComputeEvent) {
        log.info("MetricComputeA10000396_JC014 onSubscribe");
        compute(metricComputeEvent);
    }

    @Override
    public void calculate(MetricComputeEvent event, RiskControlStrategy strategy) {
        // 1. 计算系统剩余逾期本金+利息
        Triple<BigDecimal, BigDecimal, BigDecimal> triple = remainingPrincipalServiceImpl.overdueAmount(event.getSnapshotDate());
        //这里只取系统剩余逾期本金
        Pair<BigDecimal, BigDecimal> pair = Pair.of(triple.getRight(), triple.getLeft());

        // 2. 计算系统剩余本金+利息
        Map<Long, Pair<BigDecimal, BigDecimal>> prePrincipalInterest = remainingPrincipalServiceImpl.prePrincipalInterest(event.getSnapshotDate());
        // 取出结果加总就是剩余本金+利息
        BigDecimal totalRemainingInterest = prePrincipalInterest.values().stream().map(Pair::getValue).reduce(BigDecimal::add).get();
        BigDecimal totalRemainingPrincipal = prePrincipalInterest.values().stream().map(Pair::getValue).reduce(BigDecimal::add).get();

        // 3. 计算R
        BigDecimal bigDecimal = totalRemainingPrincipal.add(totalRemainingInterest).add(pair.getValue()).add(pair.getKey());
        BigDecimal resDecimal = pair.getKey().add(pair.getValue())
                .divide(bigDecimal, 4, RoundingMode.HALF_UP);
        strategy.setCurrentValueOneDecimal(resDecimal);
        MetricComputer_4Context calculateCtx = new MetricComputer_4Context();
        calculateCtx.setOverdueAmount(pair.getKey().add(pair.getValue()).longValue());
        calculateCtx.setTotalRemainingAmount(bigDecimal.toPlainString());
        calculateCtx.setLastFullComputeDate(LocalDate.now());
        strategy.setQuickContext(JSON.toJSONString(calculateCtx));
    }

    @Data
    static class MetricComputer_4Context {
        private Long overdueAmount;
        private String totalRemainingAmount;
        private LocalDate lastFullComputeDate;

    }
}
