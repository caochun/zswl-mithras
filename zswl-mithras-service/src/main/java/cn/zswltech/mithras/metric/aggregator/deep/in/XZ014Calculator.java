package cn.zswltech.mithras.metric.aggregator.deep.in;

import cn.zswltech.mithras.metric.aggregator.MetricCalculator;
import cn.zswltech.mithras.metric.mapper.model.RiskMetricTimed;
import cn.zswltech.mithras.metric.service.RiskMetricTimedService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static cn.zswltech.mithras.metric.enums.risk.index.RiskMetricLevel5.*;

/**
 * @author yibin
 */
@Component
public class XZ014Calculator implements MetricCalculator {

    @Resource
    private RiskMetricTimedService metricTimedService;

    @Override
    public String metricCode() {
        return "A10000396_XZ014";
    }

    @Override
    public Map<String, Long> varMap(LocalDate date) {
        Map<String, Long> result = new HashMap<>(1);

        QueryWrapper<RiskMetricTimed> wrapper = Wrappers.query();
        wrapper.eq("data_time", date);
        wrapper.in("level_5_type", SECONDARY.name(), SUSPICIOUS.name(), LOSS.name());
        wrapper.select("ifnull(sum(left_capital),0) as s");
        Map<String, Object> map = metricTimedService.getMap(wrapper);
        result.put("a", Long.valueOf(map.get("s").toString()));
        return result;
    }


    /**
     * 五级分类后三类剩余本金之和
     */
    @Override
    public String calcExpression() {
        return "${a}";
    }
}
