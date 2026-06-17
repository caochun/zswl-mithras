package cn.zswltech.mithras.others.job;

import cn.zswltech.mithras.others.service.ApplicationTest;
import cn.zswltech.mithras.payment.job.PaymentJob;
import org.junit.Test;

import javax.annotation.Resource;

/**
 * @author dingqi
 * @date 2025/1/13
 * @description
 */
public class PaymentJobTest extends ApplicationTest {
    @Resource
    private PaymentJob paymentJob;

    @Test
    public void calculateBeyondDaysTest() {
        paymentJob.calculateBeyondDays();
    }
}
