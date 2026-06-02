package cn.zswltech.mithras.metric.aggregator.deep.in;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import cn.zswltech.mithras.metric.aggregator.MetricCalculator;
import cn.zswltech.mithras.metric.mapper.model.RiskMetricTimed;
import cn.zswltech.mithras.metric.service.RiskMetricTimedService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.Map;

/**
 * @author yibin
 */
@Component
public class XZ016Calculator implements MetricCalculator {

    @Resource
    private RiskMetricTimedService metricTimedService;

    @Override
    public String metricCode() {
        return "A10000396_XZ016";
    }

    @Override
    public Map<String, Long> varMap(LocalDate date) {
        QueryWrapper<RiskMetricTimed> wrapper = Wrappers.query();
        wrapper.eq("data_time", date);
        wrapper.select("ifnull(sum(left_capital),0) as s");
        Map<String, Object> map = metricTimedService.getMap(wrapper);
        String s = map.get("s").toString();
        if (StrUtil.isBlank(s)) {
            return MapUtil.of("a", 0L);
        } else {
            return MapUtil.of("a", Long.valueOf(s));
        }
    }


    /**
     * 所有在租项目剩余本金之和
     */
    @Override
    public String calcExpression() {
        return "${a}";
    }
}
