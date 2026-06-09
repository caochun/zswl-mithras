package cn.zswltech.mithras.riskcontrol.metric.subscriber;

import cn.zswltech.mithras.riskcontrol.application.RiskMetricFactorQueryService;
import cn.zswltech.mithras.riskcontrol.application.RiskMetricFactorValue;
import cn.zswltech.mithras.riskcontrol.strategy.RiskControlStrategy;
import cn.zswltech.mithras.riskcontrol.metric.AbstractMetricComputer;
import cn.zswltech.mithras.riskcontrol.metric.MetricComputeEvent;
import cn.zswltech.mithras.riskcontrol.metric.SubscribeSupporter;
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
    private RiskMetricFactorQueryService factorService;

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
        RiskMetricFactorValue factorOne = factorService.newestFactor(FACTOR_NAME_ONE, FACTOR_TABLE, event.getFactorQueryDate());
        RiskMetricFactorValue factorTwo = factorService.newestFactor(FACTOR_NAME_TWO, FACTOR_TABLE, event.getFactorQueryDate());

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
