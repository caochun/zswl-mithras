package cn.zswltech.mithras.service.facade.payment;

import cn.zswltech.mithras.service.mapper.model.payment.PaymentActualDetail;
import cn.zswltech.mithras.service.mapper.model.payment.PaymentBaseInfo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import java.util.Collection;
import java.util.List;

/**
 * Payment domain facade.
 * All cross-domain access to payment data should go through this interface.
 */
public interface PaymentFacade {

    // ========== PaymentBaseInfo ==========

    PaymentBaseInfo getPaymentById(Long id);
    List<PaymentBaseInfo> listPaymentByIds(Collection<Long> ids);
    List<PaymentBaseInfo> listPayment(LambdaQueryWrapper<PaymentBaseInfo> wrapper);

    // ========== PaymentActualDetail ==========

    List<PaymentActualDetail> listActualDetail(LambdaQueryWrapper<PaymentActualDetail> wrapper);
    List<PaymentActualDetail> listActualDetailByPaymentId(Long paymentId);
}
