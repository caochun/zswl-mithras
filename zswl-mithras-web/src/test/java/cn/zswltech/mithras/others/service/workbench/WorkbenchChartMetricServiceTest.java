package cn.zswltech.mithras.others.service.workbench;

import cn.zswltech.mithras.others.service.ApplicationTest;
import cn.zswltech.mithras.workbench.application.job.WorkbenchChartMetricService;
import org.junit.Test;

import javax.annotation.Resource;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2023/5/15 22:00
 */
public class WorkbenchChartMetricServiceTest extends ApplicationTest {
    @Resource
    private WorkbenchChartMetricService workbenchChartMetricService;

    @Test
    public void testWorkbenchMetricJobHandler() {
        workbenchChartMetricService.workbenchMetricJobHandler();
    }
}
