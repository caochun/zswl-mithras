package cn.zswltech.mithras.metric.aggregator;

import cn.zswltech.mithras.metric.mapper.model.RiskMetricFactor;
import cn.zswltech.mithras.metric.mapper.model.RiskMetricTimed;
import cn.zswltech.mithras.metric.service.RiskMetricFactorQueryService;
import cn.zswltech.mithras.metric.service.RiskMetricTimedService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static cn.zswltech.mithras.metric.enums.risk.index.RiskMetricFactorTable.SUBJECT_BALANCE;
import static cn.zswltech.mithras.metric.enums.risk.index.RiskMetricLevel5.*;

/**
 * @author yibin
 */
@Component
public class JC017Calculator implements MetricCalculator {

    @Resource
    private RiskMetricFactorQueryService metricFactorService;
    @Resource
    private RiskMetricTimedService metricTimedService;

    @Override
    public String metricCode() {
        return "A10000396_JC017";
    }

    @Override
    public Map<String, Long> varMap(LocalDate date) {
        Map<String, Long> result = new HashMap<>(2);
        RiskMetricFactor factor1 = metricFactorService.getOne(Wrappers.<RiskMetricFactor>lambdaQuery()
                .eq(RiskMetricFactor::getFactorDate, date)
                .eq(RiskMetricFactor::getFactorName, "坏账准备_长期应收款坏账准备@期末余额@贷方金额")
                .eq(RiskMetricFactor::getFactorTable, SUBJECT_BALANCE.display)
        );
        result.put("a", factor1.getFactorValue());
        //
        QueryWrapper<RiskMetricTimed> wrapper = Wrappers.query();
        wrapper.eq("data_time", date);
        wrapper.in("level_5_type", SECONDARY.name(), SUSPICIOUS.name(), LOSS.name());
        wrapper.select("ifnull(sum(left_capital),0) as s");
        Map<String, Object> map = metricTimedService.getMap(wrapper);
        result.put("b", Long.valueOf(map.get("s").toString()));
        return result;
    }


    /**
     * 科目余额表（坏账准备_长期应收款坏账准备@期末余额@贷方金额）/不良资产（后三类）剩余本金之和
     */
    @Override
    public String calcExpression() {
        return "${a/b}";
    }
}
