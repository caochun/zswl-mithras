package cn.zswltech.mithras.customer.util;

import cn.hutool.core.map.MapUtil;
import de.odysseus.el.ExpressionFactoryImpl;
import de.odysseus.el.util.SimpleContext;
import lombok.extern.slf4j.Slf4j;

import javax.el.ExpressionFactory;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * @author junke
 */
@Slf4j
public class EvalUtil {
    private final static ExpressionFactory FACTORY = new ExpressionFactoryImpl();

    public static void main(String[] args) {
        HashMap<String, String> map = MapUtil.of("A9001", "${G9152/G9130}");
        HashMap<String, BigDecimal> a = MapUtil.of("G9152", new BigDecimal("1.00"));
        a.put("G9130", new BigDecimal("0.32"));
        Map<String, BigDecimal> dd = eval(map, a);

        System.out.println(dd);


    }

    public static Map<String, BigDecimal> eval(Map<String, String> expressionMap, Map<String, BigDecimal> allData) {
        Map<String, BigDecimal> result = new HashMap<>();
        SimpleContext context = new SimpleContext();
        allData.forEach((k, v) -> {
            if (null != v) {
                context.setVariable(k, FACTORY.createValueExpression(v, v.getClass()));
            }
        });
        expressionMap.forEach((subjectCode, expression) -> {
            try {
                String secondExp = (String) FACTORY.createValueExpression(context, expression, String.class).getValue(context);
                result.put(subjectCode, new BigDecimal(secondExp));
            } catch (Exception e) {
                result.put(subjectCode, null);
                log.warn("计算业务指标错误; 目标科目:{}, 表达式:{}, exception:{}", subjectCode, expression, e.getMessage());
            }
        });
        return result;
    }
}
