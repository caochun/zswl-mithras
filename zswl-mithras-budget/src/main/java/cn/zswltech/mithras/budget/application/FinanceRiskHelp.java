package cn.zswltech.mithras.budget.application;

import cn.hutool.core.util.ObjectUtil;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;

/**
 * 预算考核风险公式计算工具。
 */
@Service
public class FinanceRiskHelp {

    public BigDecimal getRiskValue(Map<String, BigDecimal> riskTree, String riskFormula) {
        BigDecimal value = BigDecimal.ZERO;
        if (ObjectUtil.isEmpty(riskFormula)) {
            return value;
        }
        String[] split = riskFormula.split("\\+");
        for (String addString : split) {
            value = value.add(riskTree.getOrDefault(addString, BigDecimal.ZERO));
        }
        return value;
    }
}
