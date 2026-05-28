package cn.zswltech.mithras.others.service.metric;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.dto.financialcloudmetric.FinancialCloudMetricValueListREQ;
import cn.zswltech.mithras.dto.financialcloudmetric.FinancialCloudMetricValueListRSP;
import cn.zswltech.mithras.metric.financialcloudmetric.mapper.FinancialCloudMetricMapper;
import cn.zswltech.mithras.metric.financialcloudmetric.model.FinancialCloudMetric;
import cn.zswltech.mithras.metric.financialcloudmetric.service.FinancialCloudMetricService;
import cn.zswltech.mithras.metric.financialcloudmetric.service.FinancialCloudMetricValueService;
import cn.zswltech.mithras.others.service.ApplicationTest;
import org.junit.Test;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2023/4/12 14:09
 */
public class CloudMetricTest extends ApplicationTest {
    @Resource
    private FinancialCloudMetricMapper financialCloudMetricMapper;
    @Resource
    private FinancialCloudMetricService service;
    @Resource
    private FinancialCloudMetricValueService valueService;

    @Test
    public void test() {

        List<FinancialCloudMetric> list = service.list();
        for (FinancialCloudMetric financialCloudMetric : list) {
            financialCloudMetric.setMetricCode("FCM_" + String.format("%03d", financialCloudMetric.getId()));
        }
        service.updateBatchById(list);
    }

    @Test
    public void xxljobTest() {
        service.financialCloudMetricJobHandler(LocalDate.now().minusMonths(1).with(TemporalAdjusters.firstDayOfMonth()));
    }

    @Test
    public void caclTest() {
        LocalDate now = LocalDate.now().minusMonths(1);
        LocalDate thisMonth = LocalDate.of(now.getYear(), now.getMonth(), 1);
        valueService.calc(thisMonth);

    }

    @Test
    public void lisTest() {
        FinancialCloudMetricValueListREQ req = new FinancialCloudMetricValueListREQ();
        req.setDataTime(LocalDate.now().with(TemporalAdjusters.firstDayOfMonth()));
        PageR<FinancialCloudMetricValueListRSP> list = valueService.list(new FinancialCloudMetricValueListREQ());
        System.out.println(list);
    }

    @Test
    public void reportTest() {
        valueService.report(LocalDate.now().with(TemporalAdjusters.firstDayOfMonth()));
    }

}
