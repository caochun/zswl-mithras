package cn.zswltech.mithras.service.service.datacompare.factory;

import cn.zswltech.mithras.api.payment.dto.PaymentDetailRsp;
import cn.zswltech.mithras.api.payment.dto.PlanedDetailDto;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.mapper.lib.CommonVersionMapper;
import cn.zswltech.mithras.service.mapper.lib.payment.PaymentBaseInfoLibMapper;
import cn.zswltech.mithras.service.mapper.lib.payment.PaymentPlanedDetailLibMapper;
import cn.zswltech.mithras.service.mapper.model.payment.PaymentBaseInfo;
import cn.zswltech.mithras.service.mapper.model.payment.PaymentBaseInfoLib;
import cn.zswltech.mithras.service.mapper.model.payment.PaymentPlanedDetail;
import cn.zswltech.mithras.service.mapper.model.payment.PaymentPlanedDetailLib;
import cn.zswltech.mithras.service.service.datacompare.AbstractDataCompare;
import cn.zswltech.mithras.service.service.datacompare.EditdataCompareFactory;
import cn.zswltech.mithras.service.service.datacompare.compare.DefaultDataCompare;
import cn.zswltech.mithras.service.service.lib.payment.handler.PaymentBaseInfoLibHandler;
import cn.zswltech.mithras.service.service.lib.payment.handler.PaymentPlanedDetailLibHandler;
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
        return new DefaultDataCompare<PaymentPlanedDetail, PaymentPlanedDetailLib, PlanedDetailDto>(rsps, libMapper, handler, commonVersionMapper, BusinessModuleEnum.PAYMENT.name(), version);
    }
}
