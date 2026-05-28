package cn.zswltech.mithras.service.service.lib.payment.handler;

import cn.zswltech.mithras.api.payment.dto.PlanedDetailDto;
import cn.zswltech.mithras.service.convert.payment.PaymentPlanedDetailConverter;
import cn.zswltech.mithras.service.mapper.model.payment.PaymentPlanedDetail;
import cn.zswltech.mithras.service.mapper.model.payment.PaymentPlanedDetailLib;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author zhaozhengkang
 * @description
 * @since
 */
@Service
public class PaymentPlanedDetailLibHandler
        extends PaymentAbstractHandler<PaymentPlanedDetailLib, PaymentPlanedDetail, PlanedDetailDto> {
    @Resource
    private PaymentPlanedDetailConverter planedDetailConverter;

    @Override
    protected PaymentPlanedDetailLib entity2Lib(PaymentPlanedDetail f) {
        return planedDetailConverter.entity2Lib(f);
    }

    @Override
    protected PaymentPlanedDetail lib2Entity(PaymentPlanedDetailLib t) {
        return planedDetailConverter.lib2Entity(t);
    }

    @Override
    protected PlanedDetailDto lib2Rsp(PaymentPlanedDetailLib f) {
        PlanedDetailDto rsp = planedDetailConverter.lib2Dto(f);
        rsp.setId(f.getOriginId());
        return rsp;
    }

    @Override
    public PaymentInfoModule getSubModule() {
        return PaymentInfoModule.PLANED_DETAILS;
    }

    @Override
    public boolean needHandle(Long clientId) {
        return true;
    }
}
