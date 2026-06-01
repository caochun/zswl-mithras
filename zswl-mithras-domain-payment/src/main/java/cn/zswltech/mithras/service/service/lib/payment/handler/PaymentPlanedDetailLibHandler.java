package cn.zswltech.mithras.service.service.lib.payment.handler;

import cn.zswltech.mithras.api.payment.dto.PlanedDetailDto;
import cn.zswltech.mithras.service.mapper.model.payment.PaymentPlanedDetail;
import cn.zswltech.mithras.service.mapper.model.payment.PaymentPlanedDetailLib;
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
