package cn.zswltech.mithras.others.service.job;

import cn.zswltech.mithras.others.service.ApplicationTest;
import cn.zswltech.mithras.service.job.FinanceJob;
import cn.zswltech.mithras.service.job.FinancingRepayInfoJob;
import org.junit.Test;

import javax.annotation.Resource;

/**
 * @author dingqi
 * @date 2023/9/11
 * @description
 */
public class FinanceJobTest extends ApplicationTest {
    @Resource
    private FinanceJob financeJob;

    @Resource
    private FinancingRepayInfoJob financingRepayInfoJob;

    @Test
    public void projectProfitCalTest() {
        financeJob.calculateProjectProfit();
    }

    @Test
    public void cancelWriteRecordAll() {
        financeJob.cancelWriteRecordAll();
    }

    @Test
    public void syncFinancingRepay() {
        financingRepayInfoJob.syncFinancingRepay();
    }


}
