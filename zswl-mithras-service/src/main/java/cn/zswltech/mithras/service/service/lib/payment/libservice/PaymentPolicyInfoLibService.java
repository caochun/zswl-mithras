package cn.zswltech.mithras.service.service.lib.payment.libservice;

import cn.zswltech.mithras.service.mapper.model.payment.PaymentPolicyInfo;
import cn.zswltech.mithras.service.mapper.model.payment.PaymentPolicyInfoLib;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

public interface PaymentPolicyInfoLibService extends IService<PaymentPolicyInfoLib> {

    Page<PaymentPolicyInfo> getByPaymentIdAndVersion(Long paymentId, String version, Integer page, Integer pageSize, Boolean adventFlag);
}
