package cn.zswltech.mithras.metric.aggregator;

import cn.hutool.core.map.MapUtil;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Map;

/**
 * @author yibin
 */
@Component
public class JC020Calculator implements MetricCalculator {

    @Override
    public String metricCode() {
        return "A10000396_JC020";
    }

    @Override
    public Map<String, Long> varMap(LocalDate date) {
        return MapUtil.empty();
    }


    /**
     * 0
     */
    @Override
    public String calcExpression() {
        return "0";
    }
}
