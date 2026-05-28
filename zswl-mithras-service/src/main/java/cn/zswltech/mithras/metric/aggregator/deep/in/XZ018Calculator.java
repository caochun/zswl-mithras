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

import static cn.zswltech.mithras.metric.enums.risk.index.RiskMetricFactorTable.CAPITAL_BALANCE;

/**
 * @author yibin
 */
@Component
public class XZ018Calculator implements MetricCalculator {

    @Resource
    private RiskMetricFactorService metricFactorService;

    @Override
    public String metricCode() {
        return "A10000396_XZ018";
    }

    @Override
    public Map<String, Long> varMap(LocalDate date) {
        Map<String, Long> result = new HashMap<>(1);
        //
        RiskMetricFactor factor = metricFactorService.getOne(Wrappers.<RiskMetricFactor>lambdaQuery()
                .eq(RiskMetricFactor::getFactorDate, date)
                .eq(RiskMetricFactor::getFactorName, "流动资产合计@期末余额")
                .eq(RiskMetricFactor::getFactorTable, CAPITAL_BALANCE.display)
        );
        result.put("a", factor.getFactorValue());
        return result;
    }


    /**
     * 资产负债表（流动资产合计@期末余额）
     */
    @Override
    public String calcExpression() {
        return "${a}";
    }
}
