package cn.zswltech.mithras.others.service.job;

import cn.zswltech.mithras.others.service.ApplicationTest;
import cn.zswltech.mithras.service.job.ProfitCalculateJob;
import org.junit.Test;

import javax.annotation.Resource;

/**
 * @author dingqi
 * @date 2023/6/28
 * @description
 */
public class ProfitCalculateJobTest extends ApplicationTest {
    @Resource
    private ProfitCalculateJob profitCalculateJob;

    @Test
    public void calculateTest() {
        profitCalculateJob.profitCalculate();
    }
}
