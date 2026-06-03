package cn.zswltech.mithras.metric.aggregator.deep.in;

import cn.zswltech.mithras.metric.aggregator.MetricCalculator;
import cn.zswltech.mithras.metric.mapper.model.RiskMetricFactor;
import cn.zswltech.mithras.metric.service.RiskMetricFactorQueryService;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static cn.zswltech.mithras.metric.enums.risk.index.RiskMetricFactorTable.SUBJECT_BALANCE;

/**
 * @author yibin
 */
@Component
public class XZ057Calculator implements MetricCalculator {

    @Resource
    private RiskMetricFactorQueryService metricFactorService;

    @Override
    public String metricCode() {
        return "A10000396_XZ057";
    }

    @Override
    public Map<String, Long> varMap(LocalDate date) {
        Map<String, Long> result = new HashMap<>(1);
        //
        RiskMetricFactor factor = metricFactorService.getOne(Wrappers.<RiskMetricFactor>lambdaQuery()
                .eq(RiskMetricFactor::getFactorDate, date)
                .eq(RiskMetricFactor::getFactorName, "债权投资_债券投资@期末余额@借方金额")
                .eq(RiskMetricFactor::getFactorTable, SUBJECT_BALANCE.display)
        );
        result.put("a", null == factor ? 0 : factor.getFactorValue());
        //

        factor = metricFactorService.getOne(Wrappers.<RiskMetricFactor>lambdaQuery()
                .eq(RiskMetricFactor::getFactorDate, date)
                .eq(RiskMetricFactor::getFactorName, "其他债权投资_债券投资@期末余额@借方金额")
                .eq(RiskMetricFactor::getFactorTable, SUBJECT_BALANCE.display)
        );
        result.put("b", null == factor ? 0 : factor.getFactorValue());
        return result;
    }


    /**
     * 科目余额表（债权投资_债券投资@期末余额@借方金额+其他债权投资_债券投资@期末余额@借方金额）
     */
    @Override
    public String calcExpression() {
        return "${a+b}";
    }
}
