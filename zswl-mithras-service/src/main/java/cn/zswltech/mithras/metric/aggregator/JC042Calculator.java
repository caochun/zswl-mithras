package cn.zswltech.mithras.metric.aggregator;

import cn.zswltech.mithras.metric.mapper.model.RiskMetricTimed;
import cn.zswltech.mithras.metric.service.RiskMetricTimedService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static cn.zswltech.mithras.metric.aggregator.AggConst.JIAO_TOU_ID;

/**
 * @author yibin
 */
@Component
public class JC042Calculator implements MetricCalculator {

    @Resource
    private RiskMetricTimedService metricTimedService;

    @Override
    public String metricCode() {
        return "A10000396_JC042";
    }

    @Override
    public Map<String, Long> varMap(LocalDate date) {
        Map<String, Long> result = new HashMap<>(1);
        //
        QueryWrapper<RiskMetricTimed> wrapper = Wrappers.query();
        wrapper.eq("data_time", date);
        wrapper.select("ifnull(sum(left_capital),0) as s");
        wrapper.eq("belong_group_id", JIAO_TOU_ID);
        wrapper.orderByDesc("s");
        Map<String, Object> map = metricTimedService.getMap(wrapper);
        if (null != map && map.size() > 0) {
            result.put("a", Long.parseLong(map.get("s").toString()));

        } else {
            result.put("a", 0L);
        }

        return result;
    }

    /**
     * 客户所属交投集团的在租项目剩余本金之和
     */
    @Override
    public String calcExpression() {
        return "${a}";
    }
}
