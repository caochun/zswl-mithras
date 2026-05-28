package cn.zswltech.mithras.metric.aggregator;

import cn.zswltech.mithras.metric.mapper.model.RiskMetricFactor;
import cn.zswltech.mithras.metric.mapper.model.RiskMetricTimed;
import cn.zswltech.mithras.metric.service.RiskMetricFactorService;
import cn.zswltech.mithras.metric.service.RiskMetricTimedService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static cn.zswltech.mithras.metric.enums.risk.index.RiskMetricFactorTable.CAPITAL_BALANCE;

/**
 * @author yibin
 */
@Component
public class JC012Calculator implements MetricCalculator {

    @Resource
    private RiskMetricFactorService metricFactorService;
    @Resource
    private RiskMetricTimedService metricTimedService;

    @Override
    public String metricCode() {
        return "A10000396_JC012";
    }

    @Override
    public Map<String, Long> varMap(LocalDate date) {
        Map<String, Long> result = new HashMap<>(2);
        //
        QueryWrapper<RiskMetricTimed> wrapper = Wrappers.query();
        wrapper.eq("data_time", date);
        wrapper.select("ifnull(sum(left_capital),0) as s");
        Map<String, Object> map = metricTimedService.getMap(wrapper);
        result.put("a", Long.valueOf(map.get("s").toString()));
        //

        RiskMetricFactor factor = metricFactorService.getOne(Wrappers.<RiskMetricFactor>lambdaQuery()
                .eq(RiskMetricFactor::getFactorDate, date)
                .eq(RiskMetricFactor::getFactorName, "所有者权益（或股东权益）合计@期末余额")
                .eq(RiskMetricFactor::getFactorTable, CAPITAL_BALANCE.display)
        );
        result.put("b", factor.getFactorValue());
        return result;
    }


    /**
     * 剩余本金/资产负债表（所有者权益（或股东权益）合计@期末余额）
     *
     * @return
     */
    @Override
    public String calcExpression() {
        return "${a/b}";
    }
}
