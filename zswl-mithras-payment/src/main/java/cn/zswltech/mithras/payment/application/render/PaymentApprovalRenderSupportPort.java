package cn.zswltech.mithras.payment.application.render;

import cn.zswltech.mithras.payment.model.PaymentBaseInfo;

public interface PaymentApprovalRenderSupportPort {

    PaymentApprovalRenderSnapshot getBaseRenderSnapshot(PaymentBaseInfo paymentBaseInfo);

    PaymentApprovalRenderSnapshot getLeaseRenderSnapshot(PaymentBaseInfo paymentBaseInfo);
}
