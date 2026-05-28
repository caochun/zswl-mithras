package cn.zswltech.mithras.metric.aggregator.deep.in;

import cn.zswltech.mithras.metric.aggregator.MetricCalculator;
import cn.zswltech.mithras.metric.mapper.model.RiskMetricFactor;
import cn.zswltech.mithras.metric.service.RiskMetricFactorService;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static cn.zswltech.mithras.metric.enums.risk.index.RiskMetricFactorTable.PROFIT;

/**
 * @author yibin
 */
@Component
public class XZ055Calculator implements MetricCalculator {

    @Resource
    private RiskMetricFactorService metricFactorService;

    @Override
    public String metricCode() {
        return "A10000396_XZ055";
    }

    @Override
    public Map<String, Long> varMap(LocalDate date) {
        Map<String, Long> result = new HashMap<>(1);
        //
        RiskMetricFactor factor = metricFactorService.getOne(Wrappers.<RiskMetricFactor>lambdaQuery()
                .eq(RiskMetricFactor::getFactorDate, date)
                .eq(RiskMetricFactor::getFactorName, "投资收益（损失以“－”号填列）@本年累计数")
                .eq(RiskMetricFactor::getFactorTable, PROFIT.display)
        );
        result.put("a", null == factor ? 0L : factor.getFactorValue());
        return result;
    }


    /**
     * 利润表（投资收益（损失以“－”号填列）@本年累计数）
     */
    @Override
    public String calcExpression() {
        return "${a}";
    }
}
