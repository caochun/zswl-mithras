package cn.zswltech.mithras.service.job;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.zswltech.mithras.payment.domain.enums.PaymentStatusEnum;
import cn.zswltech.mithras.payment.domain.enums.PaymentWriteOffStatus;
import cn.zswltech.mithras.payment.infrastructure.persistence.mapper.model.PaymentBaseInfo;
import cn.zswltech.mithras.service.service.payment.PaymentActualDetailUnconfirmedService;
import cn.zswltech.mithras.service.service.payment.PaymentBaseInfoService;
import cn.zswltech.mithras.payment.application.pubinfo.PublicInfoQueryService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;

/**
 * @author dingqi
 * @date 2025/1/13
 * @description
 */
@Slf4j
@Component
public class PaymentJob {
    @Resource
    private PublicInfoQueryService publicInfoQueryService;
    @Resource
    private PaymentBaseInfoService paymentBaseInfoService;
    @Resource
    private PaymentActualDetailUnconfirmedService paymentActualDetailUnconfirmedService;

    @XxlJob("calculatePaymentBeyondDays")
    public void calculateBeyondDays() {
        String paymentCode = XxlJobHelper.getJobParam();
        LambdaQueryWrapper<PaymentBaseInfo> query = Wrappers.lambdaQuery();
        if (StrUtil.isNotBlank(paymentCode)) {
            query.eq(PaymentBaseInfo::getPaymentCode, paymentCode);
        }
        query.eq(PaymentBaseInfo::getPaymentStatus, PaymentStatusEnum.TAKE_EFFECT.name());
        query.ne(PaymentBaseInfo::getWriteOffStatus, PaymentWriteOffStatus.WRITTEN_OFF.name());
        List<PaymentBaseInfo> todoList = paymentBaseInfoService.list(query);
        if (CollectionUtil.isEmpty(todoList)) {
            return;
        }
        List<PaymentBaseInfo> updateList = new LinkedList<>();
        for (PaymentBaseInfo paymentBaseInfo : todoList) {
            try {
                long days = paymentActualDetailUnconfirmedService.calculateEffectDays(paymentBaseInfo.getId(), LocalDate.now());
                if (days <= 0) {
                    continue;
                }
                PaymentBaseInfo update = new PaymentBaseInfo();
                update.setId(paymentBaseInfo.getId());
                update.setBeyondDays((int) days);
                updateList.add(update);
            } catch (Exception e) {
                log.error("{}计算超期天数发生异常", paymentBaseInfo.getPaymentCode(), e);
            }
        }
        if (CollectionUtil.isNotEmpty(updateList)) {
            paymentBaseInfoService.updateBatchById(updateList);
        }
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
        publicInfoQueryService.copyIntervalTable(Long.parseLong(paymentId));
    }
}
