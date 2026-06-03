package cn.zswltech.mithras.payment.application.lib.handler;

import cn.zswltech.mithras.api.payment.dto.PlanedDetailDto;
import cn.zswltech.mithras.payment.infrastructure.persistence.mapper.model.PaymentPlanedDetail;
import cn.zswltech.mithras.payment.infrastructure.persistence.mapper.model.PaymentPlanedDetailLib;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

/**
 * @author zhaozhengkang
 * @description
 * @since
 */
@Service
public class PaymentPlanedDetailLibHandler
        extends PaymentAbstractHandler<PaymentPlanedDetailLib, PaymentPlanedDetail, PlanedDetailDto> {

    @Override
    protected PaymentPlanedDetailLib entity2Lib(PaymentPlanedDetail f) {
        PaymentPlanedDetailLib lib = new PaymentPlanedDetailLib();
        BeanUtils.copyProperties(f, lib);
        return lib;
    }

    @Override
    protected PaymentPlanedDetail lib2Entity(PaymentPlanedDetailLib t) {
        PaymentPlanedDetail entity = new PaymentPlanedDetail();
        BeanUtils.copyProperties(t, entity);
        return entity;
    }

    @Override
    protected PlanedDetailDto lib2Rsp(PaymentPlanedDetailLib f) {
        PlanedDetailDto rsp = new PlanedDetailDto();
        BeanUtils.copyProperties(f, rsp);
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
