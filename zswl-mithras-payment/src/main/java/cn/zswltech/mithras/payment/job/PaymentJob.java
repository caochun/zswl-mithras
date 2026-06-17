package cn.zswltech.mithras.payment.job;

import cn.hutool.core.util.StrUtil;
import cn.zswltech.mithras.payment.application.port.PaymentBeyondDaysCalculateJobPort;
import cn.zswltech.mithras.payment.application.port.PaymentPublicInfoCopyRetryJobPort;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author dingqi
 * @date 2025/1/13
 * @description
 */
@Slf4j
@Component
public class PaymentJob {
    @Resource
    private PaymentPublicInfoCopyRetryJobPort paymentPublicInfoCopyRetryJobPort;
    @Resource
    private PaymentBeyondDaysCalculateJobPort paymentBeyondDaysCalculateJobPort;

    @XxlJob("calculatePaymentBeyondDays")
    public void calculateBeyondDays() {
        String paymentCode = XxlJobHelper.getJobParam();
        paymentBeyondDaysCalculateJobPort.calculateBeyondDays(paymentCode);
    }

    /**
     * 公开信息拷贝异常后的重试机制
     */
    @XxlJob("publicInfoCopy")
    public void publicInfoCopy() {
        String paymentId = XxlJobHelper.getJobParam();
        if (StrUtil.isBlank(paymentId)) {
            return;
        }
        paymentPublicInfoCopyRetryJobPort.copyIntervalTable(Long.parseLong(paymentId));
    }
}
