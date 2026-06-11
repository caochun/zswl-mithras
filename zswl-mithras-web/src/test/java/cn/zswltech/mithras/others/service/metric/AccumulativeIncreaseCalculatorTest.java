package cn.zswltech.mithras.others.service.metric;

import cn.zswltech.mithras.metric.financialcloudmetric.calculator.accincrease.AccumulativeIncreaseCalculator;
import cn.zswltech.mithras.others.service.ApplicationTest;
import cn.zswltech.mithras.customer.versioning.CorpAddressInfoLibService;
import cn.zswltech.mithras.customer.versioning.dto.CorpAddressInfoLibDto;
import org.junit.Test;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2023/5/26 21:11
 */
public class AccumulativeIncreaseCalculatorTest extends ApplicationTest {

    @Resource
    private List<AccumulativeIncreaseCalculator> calculatorList;

    @Test
    public void test() {

        Map<String, BigDecimal> industry = new HashMap<>();
        Map<String, BigDecimal> region = new HashMap<>();

        calculatorList.forEach(calculator -> {
            BigDecimal result = calculator.calculate(LocalDate.of(2023, 4, 1));
            if (calculator.condition() == null) {
                industry.put("Total", result);
                region.put("Total", result);
            } else if (calculator.condition().getKey().name().equals("INDUSTRY")) {
                industry.put(calculator.condition().getValue(), result);
            } else if (calculator.condition().getKey().name().equals("REGION")) {
                region.put(calculator.condition().getValue(), result);
            }
        });

        BigDecimal industryTotal = industry.entrySet().stream().filter(entry -> !entry.getKey().equals("Total")).map(Map.Entry::getValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal regionTotal = region.entrySet().stream().filter(entry -> !entry.getKey().equals("Total")).map(Map.Entry::getValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (industry.get("Total").compareTo(industryTotal) != 0) {
            System.out.println("industryTotal: " + industryTotal);
            System.out.println("total: " + industry.get("Total"));
        }
        if (region.get("Total").compareTo(regionTotal) != 0) {
            System.out.println("regionTotal: " + regionTotal);
            System.out.println("total: " + region.get("Total"));
        }

    }

    @Resource
    private CorpAddressInfoLibService addressInfoLibService;

    @Test
    public void test2() {
        Set<Long> 浙江 = addressInfoLibService.getClientIdsByProvince(new CorpAddressInfoLibDto().setInProvince(Arrays.asList("330000"))).stream().collect(Collectors.toSet());
        Set<Long> 江苏 = addressInfoLibService.getClientIdsByProvince(new CorpAddressInfoLibDto().setInProvince(Arrays.asList("320000"))).stream().collect(Collectors.toSet());
        浙江.retainAll(江苏);
    }

}
