package cn.zswltech.mithras.metric.aggregator;

import cn.hutool.core.collection.CollUtil;
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
import java.util.List;
import java.util.Map;

import static cn.zswltech.mithras.metric.aggregator.AggConst.ENGINEERING_MACHINE_BROKER_INDUSTRY_CODE;
import static cn.zswltech.mithras.metric.enums.risk.index.RiskMetricFactorTable.CAPITAL_BALANCE;
import static cn.zswltech.mithras.foundation.util.StringUtil.mysqlLimit;

/**
 * @author yibin
 */
@Component
public class JC023Calculator implements MetricCalculator {

    @Resource
    private RiskMetricFactorQueryService metricFactorService;
    @Resource
    private RiskMetricTimedService metricTimedService;

    @Override
    public String metricCode() {
        return "A10000396_JC023";
    }

    @Override
    public Map<String, Long> varMap(LocalDate date) {
        Map<String, Long> result = new HashMap<>(2);
        //
        QueryWrapper<RiskMetricTimed> wrapper = Wrappers.query();
        wrapper.eq("data_time", date);
        wrapper.select("ifnull(sum(left_capital),0) as s");
        wrapper.eq("industry_type", ENGINEERING_MACHINE_BROKER_INDUSTRY_CODE);
        wrapper.groupBy("client_id");
        wrapper.orderByDesc("s");
        wrapper.last(mysqlLimit(0, 1));
        List<Map<String, Object>> maps = metricTimedService.listMaps(wrapper);
        if (CollUtil.isEmpty(maps)) {
            result.put("a", 0L);
        } else {
            Map<String, Object> map = maps.get(0);
            result.put("a", Long.valueOf(map.get("s").toString()));
        }
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
     * 客户维度计算本金余额（只计算工程机械-经销商授信类），取剩余本金最大的客户余额/资产负债表（所有者权益（或股东权益）合计@期末余额）
     */
    @Override
    public String calcExpression() {
        return "${a/b}";
    }
}
