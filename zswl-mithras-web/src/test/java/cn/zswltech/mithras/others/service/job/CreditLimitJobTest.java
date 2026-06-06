package cn.zswltech.mithras.others.service.job;

import cn.zswltech.mithras.others.service.ApplicationTest;
import cn.zswltech.mithras.creditlimit.job.CreditLimitJob;
import org.junit.Test;

import javax.annotation.Resource;

/**
 * @author dingqi
 * @date 2024/10/14
 * @description
 */
public class CreditLimitJobTest extends ApplicationTest {
    @Resource
    private CreditLimitJob creditLimitJob;

    @Test
    public void creditLimitStatusDailyJobTest() {
        creditLimitJob.creditLimitStatusDailyJob();
    }
}
