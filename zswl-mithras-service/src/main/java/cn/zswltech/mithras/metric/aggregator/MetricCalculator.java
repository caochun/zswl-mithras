package cn.zswltech.mithras.metric.aggregator;

import cn.zswltech.mithras.metric.enums.risk.index.RiskMetricUnit;
import cn.zswltech.mithras.metric.mapper.model.RiskMetric;
import cn.zswltech.mithras.metric.service.RiskMetricService;
import cn.zswltech.mithras.service.others.SpringContextHolder;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import de.odysseus.el.ExpressionFactoryImpl;
import de.odysseus.el.util.SimpleContext;
import org.springframework.stereotype.Component;

import javax.el.ExpressionFactory;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

import static cn.hutool.core.util.ObjectUtil.isNotNull;
import static cn.zswltech.mithras.metric.enums.risk.index.RiskMetricUnit.PERCENT;
import static cn.zswltech.mithras.metric.enums.risk.index.RiskMetricUnit.WAN;

/**
 * @author yibin
 */
@Component
public interface MetricCalculator {
    ExpressionFactory FACTORY = new ExpressionFactoryImpl();

    /**
     * 指标编码
     *
     * @return 指标编码
     */
    String metricCode();

    /**
     * 计算
     *
     * @return 计算结果
     */
    default BigDecimal calc(LocalDate date) {
        SimpleContext context = new SimpleContext();
        varMap(date).forEach((k, v) -> {
            if (null != v) {
                context.setVariable(k, FACTORY.createValueExpression(v, v.getClass()));
            }
        });
        String secondExp = (String) FACTORY.createValueExpression(context, calcExpression(), String.class).getValue(context);
        BigDecimal result = new BigDecimal(secondExp);
        //单位转换
        RiskMetricService service = SpringContextHolder.getBean(RiskMetricService.class);
        RiskMetric one = service.getOne(Wrappers.<RiskMetric>lambdaQuery().eq(RiskMetric::getMetricCode, metricCode()));
        if (isNotNull(one)) {
            String unit = one.getUnit();
            RiskMetricUnit riskMetricUnit = RiskMetricUnit.valueOf(unit);
            if (riskMetricUnit == WAN) {
                result = result.divide(new BigDecimal(10000L));
            }
            if (riskMetricUnit == PERCENT) {
                result = result.multiply(new BigDecimal(100L));
                //扩大一万倍存储
                result = result.multiply(new BigDecimal(10000L));
            }
            if(riskMetricUnit == RiskMetricUnit.RATE){
                result = result.multiply(new BigDecimal(10000L));
            }
        }
        return result;
    }


    /**
     * 公式中需要的变量的map
     *
     * @return 变量map
     */
    Map<String, Long> varMap(LocalDate date);

    /**
     * 指标的计算公式
     *
     * @return 表达式
     */
    String calcExpression();

}
