package cn.zswltech.mithras.metric.aggregator;

import cn.zswltech.mithras.metric.mapper.model.RiskMetricTimed;
import cn.zswltech.mithras.metric.service.RiskMetricTimedService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static cn.zswltech.mithras.metric.aggregator.AggConst.ENGINEERING_MACHINE_RETAIL_INDUSTRY_CODE;
import static cn.zswltech.mithras.projectprocess.enums.projreview.ProjectType.PUBLIC_UTILITIES;

/**
 * @author yibin
 */
@Component
public class JC043Calculator implements MetricCalculator {

    @Resource
    private RiskMetricTimedService metricTimedService;

    @Override
    public String metricCode() {
        return "A10000396_JC043";
    }

    @Override
    public Map<String, Long> varMap(LocalDate date) {
        Map<String, Long> result = new HashMap<>(1);
        //
        QueryWrapper<RiskMetricTimed> wrapper = Wrappers.query();
        wrapper.eq("data_time", date);
        wrapper.select("ifnull(sum(left_capital),0) as s");
        wrapper.notIn("industry_type", ENGINEERING_MACHINE_RETAIL_INDUSTRY_CODE);
        wrapper.notIn("proj_type", PUBLIC_UTILITIES.name(), "PEOPLE_CONSUME");
        wrapper.groupBy("client_id");
        wrapper.orderByDesc("s");
        List<Map<String, Object>> maps = metricTimedService.listMaps(wrapper);
        if (maps.isEmpty()) {
            result.put("a", 0L);
            result.put("b", 1L);
        } else {
            result.put("a", maps.stream().mapToLong(e -> Long.parseLong(e.get("s").toString())).sum());
            result.put("b", (long) maps.size());
        }
        return result;
    }

    /**
     * 在租项目剩余本金（不含工程机械零售、 公用事业类、民生消费类项目）/客户数量(不含工程机械零售、 公用事业类、民生消费类)
     */
    @Override
    public String calcExpression() {
        return "${a/b}";
    }
}
