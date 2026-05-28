package cn.zswltech.mithras.others.service.metric;

import cn.zswltech.mithras.metric.financialcloudmetric.calculator.OverdueProjectsCalculator;
import cn.zswltech.mithras.others.service.ApplicationTest;
import org.junit.Test;

import javax.annotation.Resource;
import java.time.LocalDate;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2023/5/25 10:11
 */
public class OverdueCalculatorTest extends ApplicationTest {

    @Resource
    private OverdueProjectsCalculator overdueProjectsCalculator;

    @Test
    public void test() {
        overdueProjectsCalculator.calculate(LocalDate.of(2023, 4, 1));
    }
}
