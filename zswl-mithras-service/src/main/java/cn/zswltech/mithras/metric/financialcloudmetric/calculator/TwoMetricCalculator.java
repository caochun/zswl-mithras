package cn.zswltech.mithras.metric.financialcloudmetric.calculator;

import cn.zswltech.mithras.metric.financialcloudmetric.model.FinancialCloudMetricValue;
import cn.zswltech.mithras.common.util.StringUtil;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * @description: 两个指标间的计算
 * @author: zhaozhengkang
 * @date: 2023/5/8 14:37
 */
public abstract class TwoMetricCalculator implements FinancialCloudMetricCalculator, Secondary {

    protected BigDecimal division(FinancialCloudMetricValue left, FinancialCloudMetricValue right, int scale) {
        if (left.getValue() == null) {
            return BigDecimal.ZERO;
        }
        if (right.getValue() == null) {
            throw new MissingFactorException("指标" + right.getMetricName() + "缺失！");
        }
        if (!StringUtil.isNum(right.getValue()) || "0".equals(right.getValue())) {
            return BigDecimal.ZERO;
//            throw new MissingFactorException("指标" + right.getMetricName() + "的值不合理！");
        }
        return new BigDecimal(left.getValue())
                .multiply(new BigDecimal(1000000))
                .divide(new BigDecimal(right.getValue()), scale, RoundingMode.HALF_UP);
    }

    protected BigDecimal subtraction(FinancialCloudMetricValue left, FinancialCloudMetricValue right) {
        if (right.getValue() == null) {
            throw new MissingFactorException("指标" + right.getMetricName() + "缺失！");
        }
        return new BigDecimal(left.getMetricValue()).subtract(new BigDecimal(right.getMetricValue()));
    }
}
