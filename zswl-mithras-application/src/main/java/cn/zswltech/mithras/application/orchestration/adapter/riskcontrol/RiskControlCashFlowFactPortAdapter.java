package cn.zswltech.mithras.application.orchestration.adapter.riskcontrol;

import cn.zswltech.mithras.collection.mapper.CollectionBaseInfoMapper;
import cn.zswltech.mithras.payment.mapper.PaymentActualDetailMapper;
import cn.zswltech.mithras.riskcontrol.application.port.RiskControlCashFlowFactPort;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class RiskControlCashFlowFactPortAdapter implements RiskControlCashFlowFactPort {

    @Resource
    private PaymentActualDetailMapper paymentActualDetailMapper;
    @Resource
    private CollectionBaseInfoMapper collectionBaseInfoMapper;

    @Override
    public long totalPaidAmount() {
        return paymentActualDetailMapper.totalPay();
    }

    @Override
    public long totalFirstRentCollectionAmount() {
        return collectionBaseInfoMapper.totalFirstRentCollection();
    }

    @Override
    public long totalRentPrincipalCollectionAmount() {
        return collectionBaseInfoMapper.totalRentPrincipalCollection();
    }
}
