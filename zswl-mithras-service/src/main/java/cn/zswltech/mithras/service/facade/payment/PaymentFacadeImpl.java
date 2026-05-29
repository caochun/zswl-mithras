package cn.zswltech.mithras.service.facade.payment;

import cn.zswltech.mithras.service.mapper.model.payment.PaymentActualDetail;
import cn.zswltech.mithras.service.mapper.model.payment.PaymentBaseInfo;
import cn.zswltech.mithras.service.service.payment.PaymentActualDetailService;
import cn.zswltech.mithras.service.service.payment.PaymentBaseInfoService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Service
public class PaymentFacadeImpl implements PaymentFacade {

    @Resource
    private PaymentBaseInfoService paymentBaseInfoService;
    @Resource
    private PaymentActualDetailService paymentActualDetailService;

    @Override
    public PaymentBaseInfo getPaymentById(Long id) {
        return paymentBaseInfoService.getById(id);
    }

    @Override
    public List<PaymentBaseInfo> listPaymentByIds(Collection<Long> ids) {
        if (ids == null || ids.isEmpty()) return Collections.emptyList();
        return paymentBaseInfoService.listByIds(ids);
    }

    @Override
    public List<PaymentBaseInfo> listPayment(LambdaQueryWrapper<PaymentBaseInfo> wrapper) {
        return paymentBaseInfoService.list(wrapper);
    }

    @Override
    public List<PaymentActualDetail> listActualDetail(LambdaQueryWrapper<PaymentActualDetail> wrapper) {
        return paymentActualDetailService.list(wrapper);
    }

    @Override
    public List<PaymentActualDetail> listActualDetailByPaymentId(Long paymentId) {
        return paymentActualDetailService.list(
                Wrappers.<PaymentActualDetail>lambdaQuery().eq(PaymentActualDetail::getPaymentId, paymentId));
    }
}
