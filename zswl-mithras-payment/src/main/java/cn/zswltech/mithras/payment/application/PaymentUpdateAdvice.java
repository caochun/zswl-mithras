package cn.zswltech.mithras.payment.application;

import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.payment.domain.enums.PaymentStatusEnum;
import cn.zswltech.mithras.payment.infrastructure.persistence.mapper.PaymentBaseInfoMapper;
import cn.zswltech.mithras.payment.infrastructure.persistence.mapper.model.PaymentBaseInfo;
import cn.zswltech.mithras.service.enums.common.ProcessStatus;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.others.SpringContextHolder;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * @description Payment更新后执行的切面操作
 * @author zhaozhengkang
 * @date 2022-07-19
 */
public interface PaymentUpdateAdvice {

    default void saveCheck(PaymentBaseInfo baseInfo) {
        if (ObjectUtil.isEmpty(baseInfo)) {
            throw new MithrasException("付款申请不存在");
        }
        if (PaymentStatusEnum.TAKE_EFFECT.name().equals(baseInfo.getPaymentStatus())) {
            throw new MithrasException("付款申请生效后不可变更");
        }
    }

    default void recordStatus(Long paymentId) {
        PaymentStatusUpdatePort statusUpdatePort = SpringContextHolder.getBean(PaymentStatusUpdatePort.class);
        PaymentBaseInfoMapper baseInfoMapper = SpringContextHolder.getBean(PaymentBaseInfoMapper.class);
        boolean hasRelatedProcess = statusUpdatePort.hasRelatedProcess(paymentId);
        PaymentBaseInfo baseInfo = baseInfoMapper.selectById(paymentId);
        if (Objects.isNull(baseInfo)) {
            return;
        }
        if (PaymentStatusEnum.NEW.name().equals(baseInfo.getPaymentStatus()) || hasRelatedProcess) {
            UpdateWrapper updateWrapper = new UpdateWrapper();
            updateWrapper.eq("id", baseInfo.getId());
            updateWrapper.set("update_time", LocalDateTime.now());
            baseInfoMapper.update(null, updateWrapper);
            return;
        }
        statusUpdatePort.recordPaymentStatus(paymentId, null, ProcessStatus.UN_SUBMIT);
    }
}
