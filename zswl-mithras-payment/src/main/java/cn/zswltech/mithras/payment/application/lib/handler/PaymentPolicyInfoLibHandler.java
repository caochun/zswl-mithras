package cn.zswltech.mithras.payment.application.lib.handler;

import cn.zswltech.mithras.dto.payment.lib.PaymentPolicyInfoListRSP;
import cn.zswltech.mithras.payment.model.PaymentPolicyInfo;
import cn.zswltech.mithras.payment.model.PaymentPolicyInfoLib;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

/**
 * @author zhaozhengkang
 * @description
 * @since
 */
@Service
public class PaymentPolicyInfoLibHandler
        extends PaymentAbstractHandler<PaymentPolicyInfoLib, PaymentPolicyInfo, PaymentPolicyInfoListRSP> {

    @Override
    protected PaymentPolicyInfoLib entity2Lib(PaymentPolicyInfo f) {
        PaymentPolicyInfoLib lib = new PaymentPolicyInfoLib();
        BeanUtils.copyProperties(f, lib);
        return lib;
    }

    @Override
    protected PaymentPolicyInfo lib2Entity(PaymentPolicyInfoLib t) {
        PaymentPolicyInfo entity = new PaymentPolicyInfo();
        BeanUtils.copyProperties(t, entity);
        return entity;
    }

    @Override
    protected PaymentPolicyInfoListRSP lib2Rsp(PaymentPolicyInfoLib f) {
        PaymentPolicyInfoListRSP paymentPolicyInfoListRSP = new PaymentPolicyInfoListRSP();
        BeanUtils.copyProperties(f, paymentPolicyInfoListRSP);
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
