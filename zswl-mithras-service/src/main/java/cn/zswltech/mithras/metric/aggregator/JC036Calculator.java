package cn.zswltech.mithras.metric.aggregator;

import cn.zswltech.mithras.metric.mapper.model.RiskMetricDict;
import cn.zswltech.mithras.metric.mapper.model.RiskMetricTimed;
import cn.zswltech.mithras.metric.service.RiskMetricDictService;
import cn.zswltech.mithras.metric.service.RiskMetricTimedService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static cn.zswltech.mithras.metric.aggregator.AggConst.TRANSPORTATION_INDUSTRY_CODE;
import static cn.zswltech.mithras.service.util.StringUtil.mysqlLimit;

/**
 * @author yibin
 */
@Component
public class JC036Calculator implements MetricCalculator {

    @Resource
    private RiskMetricDictService metricDictService;
    @Resource
    private RiskMetricTimedService metricTimedService;

    @Override
    public String metricCode() {
        return "A10000396_JC036";
    }

    @Override
    public Map<String, Long> varMap(LocalDate date) {
        Map<String, Long> result = new HashMap<>(1);
        //
        RiskMetricDict dict = metricDictService.getOne(Wrappers.<RiskMetricDict>lambdaQuery()
                .eq(RiskMetricDict::getDictType, "INDUSTRY_LIMIT")
                .eq(RiskMetricDict::getDictKey, "TRANSPORTATION")
        );
        result.put("a", Long.parseLong(dict.getDickValue()) * 100_000_000L * 10000L);
        //
        QueryWrapper<RiskMetricTimed> wrapper = Wrappers.query();
        wrapper.eq("data_time", date);
        wrapper.select("ifnull(sum(left_capital),0) as s");
        wrapper.eq("industry_type", TRANSPORTATION_INDUSTRY_CODE);
        wrapper.orderByDesc("s");
        wrapper.last(mysqlLimit(0, 1));
        Map<String, Object> map = metricTimedService.getMap(wrapper);
        result.put("b", Long.valueOf(map.get("s").toString()));

        return result;
    }

    /**
     * 根据行业风险敞口额度（暂写死）-该行业在租项目剩余本金之和
     * 钢铁、不锈钢及有色金属冶炼行业
     */
    @Override
    public String calcExpression() {
        return "${a-b}";
    }
}
