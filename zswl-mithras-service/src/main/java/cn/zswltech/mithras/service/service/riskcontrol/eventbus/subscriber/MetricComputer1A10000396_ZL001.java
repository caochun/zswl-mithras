package cn.zswltech.mithras.service.service.riskcontrol.eventbus.subscriber;

import cn.zswltech.mithras.metric.mapper.model.RiskMetricFactor;
import cn.zswltech.mithras.metric.service.RiskMetricFactorService;
import cn.zswltech.mithras.riskcontrol.strategy.RiskControlStrategy;
import cn.zswltech.mithras.service.service.riskcontrol.AbstractMetricComputer;
import cn.zswltech.mithras.riskcontrol.metric.MetricComputeEvent;
import cn.zswltech.mithras.riskcontrol.metric.SubscribeSupporter;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;


/**
 * R=剩余本金之和/资产负债表「所有者权益（或股东权益）合计@期末余额」
 *
 * @author zhaozhengkang
 */
@Component
@Slf4j
public class MetricComputer1A10000396_ZL001 extends AbstractMetricComputer implements SubscribeSupporter<MetricComputeEvent> {

    @Resource
    private RiskMetricFactorService factorService;

    public static final String FACTOR_NAME_1 = "资产总计@期末余额"; //139184443481100
    public static final String FACTOR_NAME_2 = "货币资金@期末余额";//4618241429400
    public static final String FACTOR_TABLE = "资产负债表";//25003132199000

    //分母
    public static final String FACTOR_NAME = "所有者权益（或股东权益）合计@期末余额";

    @Override
    public String getMetricCode() {
        return "A10000396_ZL001";
    }

    @AllowConcurrentEvents
    @Subscribe
    @Override
    public void onSubscribe(MetricComputeEvent metricComputeEvent) {
        log.info("MetricComputeA10000396_ZL001 onSubscribe");
        compute(metricComputeEvent);
    }

    @Override
    public void calculate(MetricComputeEvent event, RiskControlStrategy strategy) {
        RiskMetricFactor factor_1 = factorService.getOne(Wrappers.<RiskMetricFactor>lambdaQuery()
                .eq(RiskMetricFactor::getFactorName, FACTOR_NAME_1)
                .eq(RiskMetricFactor::getFactorTable, FACTOR_TABLE)
                .ge(RiskMetricFactor::getFactorDate, event.getFactorQueryDate())
                .orderByDesc(RiskMetricFactor::getFactorDate)
                .last("limit 1"));
        RiskMetricFactor factor_2 = factorService.getOne(Wrappers.<RiskMetricFactor>lambdaQuery()
                .eq(RiskMetricFactor::getFactorName, FACTOR_NAME_2)
                .eq(RiskMetricFactor::getFactorTable, FACTOR_TABLE)
                .ge(RiskMetricFactor::getFactorDate, event.getFactorQueryDate())
                .orderByDesc(RiskMetricFactor::getFactorDate)
                .last("limit 1"));

        //分母
        RiskMetricFactor factor = factorService.getOne(Wrappers.<RiskMetricFactor>lambdaQuery()
                .eq(RiskMetricFactor::getFactorName, FACTOR_NAME)
                .eq(RiskMetricFactor::getFactorTable, FACTOR_TABLE)
                .ge(RiskMetricFactor::getFactorDate, event.getFactorQueryDate())
                .orderByDesc(RiskMetricFactor::getFactorDate)
                .last("limit 1"));
        if (factor_1 == null || factor_2 == null || factor == null) {
            strategy.setNullReason("上月指标因子未导入");
            strategy.setCurrentValueOne(null);
            strategy.setCurrentValueTwo(null);
            return;
        }
        //分子
        long molecule = factor_1.getFactorValue() - factor_2.getFactorValue();
        //分母
        long denominator = factor.getFactorValue();

        strategy.setCurrentValueOneDecimal(BigDecimal.valueOf(molecule).divide(new BigDecimal(denominator), 2, RoundingMode.HALF_UP));
    }
}
