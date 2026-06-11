package cn.zswltech.mithras.payment.application.lib.service;

import cn.zswltech.mithras.payment.model.PaymentPolicyInfo;
import cn.zswltech.mithras.payment.model.PaymentPolicyInfoLib;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

public interface PaymentPolicyInfoLibService extends IService<PaymentPolicyInfoLib> {

    Page<PaymentPolicyInfo> getByPaymentIdAndVersion(Long paymentId, String version, Integer page, Integer pageSize, Boolean adventFlag);
}
