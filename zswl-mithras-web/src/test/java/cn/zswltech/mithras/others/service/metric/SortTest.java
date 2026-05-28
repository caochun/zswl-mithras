package cn.zswltech.mithras.others.service.metric;

import cn.zswltech.mithras.metric.financialcloudmetric.model.FinancialCloudMetric;
import cn.zswltech.mithras.metric.financialcloudmetric.service.FinancialCloudMetricService;
import cn.zswltech.mithras.others.service.ApplicationTest;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.junit.Test;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2023/5/31 14:31
 */
public class SortTest extends ApplicationTest {
    @Resource
    private FinancialCloudMetricService financialCloudMetricService;

    @Test
    public void testSort() {
        List<FinancialCloudMetric> allMetric = financialCloudMetricService.list(Wrappers.<FinancialCloudMetric>lambdaQuery().orderByAsc(FinancialCloudMetric::getId));
        Map<String, List<FinancialCloudMetric>> collect = allMetric.stream().collect(Collectors.groupingBy(metric -> String.join(":", metric.getMetricFirstType(), metric.getMetricSecondType(), metric.getMetricName(), metric.getOneLevelType())));
        int count = 1;
        List<FinancialCloudMetric> resolved = new ArrayList<>();
        for (FinancialCloudMetric metric : allMetric) {
            String key = String.join(":", metric.getMetricFirstType(), metric.getMetricSecondType(), metric.getMetricName(), metric.getOneLevelType());
            if (collect.containsKey(key)) {
                List<FinancialCloudMetric> metrics = collect.get(key);
                for (FinancialCloudMetric financialCloudMetric : metrics) {
                    financialCloudMetric.setSortNo(count++);
                }
                resolved.addAll(metrics);
                collect.remove(key);
            }
        }
        financialCloudMetricService.updateBatchById(resolved);
        System.out.println("hello world");
    }
}
