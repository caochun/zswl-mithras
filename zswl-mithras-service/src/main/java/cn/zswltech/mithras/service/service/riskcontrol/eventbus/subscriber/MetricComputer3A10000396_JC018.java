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
 * R=资产负债表「流动资产合计@期末余额」/资产负债表「流动负债合计@期末余额」
 *
 * @author zhaozhengkang
 */
@Component
@Slf4j
public class MetricComputer3A10000396_JC018 extends AbstractMetricComputer implements SubscribeSupporter<MetricComputeEvent> {
    @Resource
    private RiskMetricFactorService factorService;

    public static final String FACTOR_NAME_ONE = "流动资产合计@期末余额";
    public static final String FACTOR_NAME_TWO = "流动负债合计@期末余额";
    public static final String FACTOR_TABLE = "资产负债表";


    @Override
    public String getMetricCode() {
        return "A10000396_JC018";
    }

    @AllowConcurrentEvents
    @Subscribe
    @Override
    public void onSubscribe(MetricComputeEvent metricComputeEvent) {
        log.info("MetricComputeA10000396_JC018 onSubscribe");
        compute(metricComputeEvent);
    }

    @Override
    public void calculate(MetricComputeEvent event, RiskControlStrategy strategy) {
        RiskMetricFactor factorOne = factorService.getOne(Wrappers.<RiskMetricFactor>lambdaQuery()
                .eq(RiskMetricFactor::getFactorName, FACTOR_NAME_ONE)
                .eq(RiskMetricFactor::getFactorTable, FACTOR_TABLE)
                .ge(RiskMetricFactor::getFactorDate, event.getFactorQueryDate())
                .orderByDesc(RiskMetricFactor::getFactorDate)
                .last("limit 1"));
        RiskMetricFactor factorTwo = factorService.getOne(Wrappers.<RiskMetricFactor>lambdaQuery()
                .eq(RiskMetricFactor::getFactorName, FACTOR_NAME_TWO)
                .eq(RiskMetricFactor::getFactorTable, FACTOR_TABLE)
                .ge(RiskMetricFactor::getFactorDate, event.getFactorQueryDate())
                .orderByDesc(RiskMetricFactor::getFactorDate)
                .last("limit 1"));

        if (factorOne == null || factorTwo == null) {
            strategy.setNullReason("上月指标因子未导入");
            strategy.setCurrentValueOne(null);
            return;
        }
        BigDecimal res = new BigDecimal(factorOne.getFactorValue())
                .divide(new BigDecimal(factorTwo.getFactorValue()), 4, RoundingMode.HALF_UP)
                .multiply(new BigDecimal(10000));
        strategy.setCurrentValueOne(res.longValue());
    }

}
