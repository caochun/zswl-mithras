package cn.zswltech.mithras.application.orchestration.adapter.riskcontrol;

import cn.zswltech.mithras.foundation.enums.common.ProjectBizType;
import cn.zswltech.mithras.payment.mapper.PaymentBaseInfoMapper;
import cn.zswltech.mithras.payment.model.PaymentBaseInfo;
import cn.zswltech.mithras.projectprocess.mapper.projestablish.ProjEstablishAocPriceMapper;
import cn.zswltech.mithras.projectprocess.mapper.projestablish.ProjEstablishBaseInfoMapper;
import cn.zswltech.mithras.projectprocess.mapper.projestablish.ProjEstablishFactoringPriceMapper;
import cn.zswltech.mithras.projectprocess.mapper.projestablish.ProjEstablishLeasePriceMapper;
import cn.zswltech.mithras.projectprocess.model.projestablish.ProjEstablishAocPrice;
import cn.zswltech.mithras.projectprocess.model.projestablish.ProjEstablishBaseInfo;
import cn.zswltech.mithras.projectprocess.model.projestablish.ProjEstablishFactoringPrice;
import cn.zswltech.mithras.projectprocess.model.projestablish.ProjEstablishLeasePrice;
import cn.zswltech.mithras.riskcontrol.application.port.RiskControlInterceptFact;
import cn.zswltech.mithras.riskcontrol.application.port.RiskControlInterceptFactPort;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Optional;

@Component
public class RiskControlInterceptFactPortAdapter implements RiskControlInterceptFactPort {

    @Resource
    private ProjEstablishBaseInfoMapper projEstablishBaseInfoMapper;
    @Resource
    private ProjEstablishLeasePriceMapper projEstablishLeasePriceMapper;
    @Resource
    private ProjEstablishFactoringPriceMapper projEstablishFactoringPriceMapper;
    @Resource
    private ProjEstablishAocPriceMapper projEstablishAocPriceMapper;
    @Resource
    private PaymentBaseInfoMapper paymentBaseInfoMapper;

    @Override
    public Optional<RiskControlInterceptFact> projectEstablishFact(Long projEstablishId) {
        ProjEstablishBaseInfo projEstablish = projEstablishBaseInfoMapper.selectById(projEstablishId);
        if (projEstablish == null) {
            return Optional.empty();
        }
        return Optional.of(new RiskControlInterceptFact(projEstablish.getClientId(), declaredAmount(projEstablish)));
    }

    @Override
    public Optional<RiskControlInterceptFact> paymentApplyFact(Long paymentId) {
        PaymentBaseInfo paymentBaseInfo = paymentBaseInfoMapper.selectById(paymentId);
        if (paymentBaseInfo == null) {
            return Optional.empty();
        }
        return Optional.of(new RiskControlInterceptFact(paymentBaseInfo.getClientId(), paymentBaseInfo.getApplyPaymentAmount()));
    }

    private Long declaredAmount(ProjEstablishBaseInfo projEstablish) {
        ProjectBizType bizType = ProjectBizType.of(projEstablish.getBizType());
        if (ProjectBizType.BL.equals(bizType)) {
            ProjEstablishFactoringPrice factoringPrice = projEstablishFactoringPriceMapper.selectByMainId(projEstablish.getId());
            return factoringPrice == null ? null : factoringPrice.getApplyCreditAmount();
        }
        if (ProjectBizType.ZR.equals(bizType)) {
            ProjEstablishAocPrice aocPrice = projEstablishAocPriceMapper.selectByMainId(projEstablish.getId());
            return aocPrice == null ? null : aocPrice.getApplyCreditAmount();
        }
        ProjEstablishLeasePrice leasePrice = projEstablishLeasePriceMapper.selectByMainId(projEstablish.getId());
        return leasePrice == null ? null : leasePrice.getApplyCreditAmount();
    }
}
