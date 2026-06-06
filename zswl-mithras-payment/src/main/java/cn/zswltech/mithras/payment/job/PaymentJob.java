package cn.zswltech.mithras.payment.job;

import cn.hutool.core.util.StrUtil;
import cn.zswltech.mithras.payment.application.job.PaymentBeyondDaysCalculateService;
import cn.zswltech.mithras.payment.application.job.PaymentPublicInfoCopyRetryService;
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
    private PaymentPublicInfoCopyRetryService paymentPublicInfoCopyRetryService;
    @Resource
    private PaymentBeyondDaysCalculateService paymentBeyondDaysCalculateService;

    @XxlJob("calculatePaymentBeyondDays")
    public void calculateBeyondDays() {
        String paymentCode = XxlJobHelper.getJobParam();
        paymentBeyondDaysCalculateService.calculateBeyondDays(paymentCode);
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
        paymentPublicInfoCopyRetryService.copyIntervalTable(Long.parseLong(paymentId));
    }
}
