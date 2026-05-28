package cn.zswltech.mithras.service.service.lib.payment.handler;

import cn.hutool.core.bean.BeanUtil;
import cn.zswltech.mithras.dto.payment.lib.PaymentPolicyInfoListRSP;
import cn.zswltech.mithras.service.convert.payment.PaymentPlanedDetailConverter;
import cn.zswltech.mithras.service.mapper.model.payment.PaymentPolicyInfo;
import cn.zswltech.mithras.service.mapper.model.payment.PaymentPolicyInfoLib;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author zhaozhengkang
 * @description
 * @since
 */
@Service
public class PaymentPolicyInfoLibHandler
        extends PaymentAbstractHandler<PaymentPolicyInfoLib, PaymentPolicyInfo, PaymentPolicyInfoListRSP> {
    @Resource
    private PaymentPlanedDetailConverter planedDetailConverter;

    @Override
    protected PaymentPolicyInfoLib entity2Lib(PaymentPolicyInfo f) {
        return BeanUtil.copyProperties(f, PaymentPolicyInfoLib.class);
    }

    @Override
    protected PaymentPolicyInfo lib2Entity(PaymentPolicyInfoLib t) {
        return BeanUtil.copyProperties(t, PaymentPolicyInfo.class);
    }

    @Override
    protected PaymentPolicyInfoListRSP lib2Rsp(PaymentPolicyInfoLib f) {
        PaymentPolicyInfoListRSP paymentPolicyInfoListRSP = BeanUtil.copyProperties(f, PaymentPolicyInfoListRSP.class);
        paymentPolicyInfoListRSP.setId(f.getOriginId());
        return paymentPolicyInfoListRSP;
    }

    @Override
    public PaymentInfoModule getSubModule() {
        return PaymentInfoModule.POLICE_INFO;
    }

    @Override
    public boolean needHandle(Long clientId) {
        return true;
    }
}
