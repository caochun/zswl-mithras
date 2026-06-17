package cn.zswltech.mithras.others.job;

import cn.zswltech.mithras.others.service.ApplicationTest;
import cn.zswltech.mithras.kpi.job.KpiJob;
import cn.zswltech.mithras.application.orchestration.monthly.MonthlyManageService;
import cn.zswltech.mithras.application.orchestration.monthly.MonthlySendCqService;
import org.junit.Test;

import javax.annotation.Resource;

/**
 * @author dingqi
 * @date 2023/6/20
 * @description
 */
public class KpiJobTest extends ApplicationTest {
    @Resource
    private KpiJob kpiJob;
    @Resource
    private MonthlySendCqService monthlyManageService;

    @Test
    public void calculateKpiProjectBonusTest() {
        kpiJob.calculateKpiProjectBonus();
    }

    @Test
    public void calculateKpiProject(){
        monthlyManageService.accountApplication("2024-06", "20240702133811");
    }
}
