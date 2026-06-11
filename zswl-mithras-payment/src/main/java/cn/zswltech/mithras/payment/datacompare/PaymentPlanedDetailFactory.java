package cn.zswltech.mithras.payment.datacompare;

import cn.zswltech.mithras.api.payment.dto.PlanedDetailDto;
import cn.zswltech.mithras.foundation.persistence.mapper.CommonVersionMapper;
import cn.zswltech.mithras.payment.mapper.lib.PaymentPlanedDetailLibMapper;
import cn.zswltech.mithras.payment.model.PaymentPlanedDetail;
import cn.zswltech.mithras.payment.model.PaymentPlanedDetailLib;
import cn.zswltech.mithras.foundation.datacompare.AbstractDataCompare;
import cn.zswltech.mithras.foundation.datacompare.EditdataCompareFactory;
import cn.zswltech.mithras.foundation.datacompare.compare.DefaultDataCompare;
import cn.zswltech.mithras.payment.application.lib.handler.PaymentPlanedDetailLibHandler;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2022/8/22 11:25
 */
@Service("paymentPlanedDetail")
public class PaymentPlanedDetailFactory implements EditdataCompareFactory {
    @Resource
    private PaymentPlanedDetailLibMapper libMapper;
    @Resource
    private PaymentPlanedDetailLibHandler handler;
    @Resource
    private CommonVersionMapper commonVersionMapper;

    @Override
    public AbstractDataCompare createCompare(List rsps, String version) {
        return new DefaultDataCompare<PaymentPlanedDetail, PaymentPlanedDetailLib, PlanedDetailDto>(rsps, libMapper, handler, commonVersionMapper, "PAYMENT", version);
    }
}
