package cn.zswltech.mithras.application.orchestration.adapter.payment;

import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.payment.application.PaymentModifyAuthPort;
import cn.zswltech.mithras.payment.enums.PaymentStatusEnum;
import cn.zswltech.mithras.payment.enums.PaymentWriteOffStatus;
import cn.zswltech.mithras.payment.mapper.PaymentBaseInfoMapper;
import cn.zswltech.mithras.payment.model.PaymentBaseInfo;
import cn.zswltech.mithras.application.orchestration.auth.checker.common.CommonModifySubAuthCheckerNew;
import cn.zswltech.mithras.application.orchestration.auth.BusinessModuleEnum;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class PaymentModifyAuthPortAdapter implements PaymentModifyAuthPort {

    @Resource
    private CommonModifySubAuthCheckerNew commonModifySubAuthChecker;
    @Resource
    private PaymentBaseInfoMapper paymentBaseInfoMapper;

    @Override
    public void checkModify(Long paymentId) {
        commonModifySubAuthChecker.check(BusinessModuleEnum.PAYMENT, PaymentBaseInfoMapper.class, paymentId, new Object[0]);
        if (ObjectUtil.isNotEmpty(paymentId)) {
            PaymentBaseInfo baseInfo = paymentBaseInfoMapper.selectById(paymentId);
            if (PaymentStatusEnum.CLOSED.name().equals(baseInfo.getPaymentStatus())) {
                throw new MithrasException("该申请已关闭，不允许再修改有关信息");
            }
            if (PaymentWriteOffStatus.WRITTEN_OFF.name().equals(baseInfo.getWriteOffStatus())) {
                throw new MithrasException("该申请已核销，不允许再修改有关信息");
            }
        }
    }
}
