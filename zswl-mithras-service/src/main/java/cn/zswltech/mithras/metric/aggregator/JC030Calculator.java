package cn.zswltech.mithras.metric.aggregator;

import cn.zswltech.mithras.metric.mapper.model.RiskMetricDict;
import cn.zswltech.mithras.metric.service.RiskMetricDictService;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

/**
 * @author yibin
 */
@Component
public class JC030Calculator implements MetricCalculator {

    @Resource
    private RiskMetricDictService metricDictService;

    @Override
    public String metricCode() {
        return "A10000396_JC030";
    }

    @Override
    public Map<String, Long> varMap(LocalDate date) {
        Map<String, Long> result = new HashMap<>(1);
        //
        RiskMetricDict dict = metricDictService.getOne(Wrappers.<RiskMetricDict>lambdaQuery()
                .eq(RiskMetricDict::getDictType, "FINANCE_PERIOD")
                .eq(RiskMetricDict::getDictKey, "SHIP_PV_EXCLUDE")
        );
        result.put("a", Long.valueOf(dict.getDickValue()));
        return result;
    }

    /**
     * 暂定写死，需各行业融资期限（待风控提供）
     * 融资期限 (船舶、光伏除外)
     */
    @Override
    public String calcExpression() {
        return "${a}";
    }
}
