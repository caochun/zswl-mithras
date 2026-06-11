package cn.zswltech.mithras.others.service.job;

import cn.zswltech.mithras.others.service.ApplicationTest;
import cn.zswltech.mithras.finance.application.job.FinanceJobService;
import cn.zswltech.mithras.application.orchestration.job.FinancingRepayInfoJob;
import org.junit.Test;

import javax.annotation.Resource;

/**
 * @author dingqi
 * @date 2023/9/11
 * @description
 */
public class FinanceJobTest extends ApplicationTest {
    @Resource
    private FinanceJobService financeJobService;

    @Resource
    private FinancingRepayInfoJob financingRepayInfoJob;

    @Test
    public void projectProfitCalTest() {
        financeJobService.calculateProjectProfit(null);
    }

    @Test
    public void cancelWriteRecordAll() {
        financeJobService.cancelWriteRecordAll();
    }

    @Test
    public void syncFinancingRepay() {
        financingRepayInfoJob.syncFinancingRepay();
    }


}
