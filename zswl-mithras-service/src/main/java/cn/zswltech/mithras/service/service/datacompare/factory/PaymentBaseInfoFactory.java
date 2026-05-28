package cn.zswltech.mithras.service.service.datacompare.factory;

import cn.zswltech.mithras.api.payment.dto.PaymentDetailRsp;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.mapper.lib.CommonVersionMapper;
import cn.zswltech.mithras.service.mapper.lib.payment.PaymentBaseInfoLibMapper;
import cn.zswltech.mithras.service.mapper.model.payment.PaymentBaseInfo;
import cn.zswltech.mithras.service.mapper.model.payment.PaymentBaseInfoLib;
import cn.zswltech.mithras.service.service.datacompare.AbstractDataCompare;
import cn.zswltech.mithras.service.service.datacompare.EditdataCompareFactory;
import cn.zswltech.mithras.service.service.datacompare.compare.DefaultDataCompare;
import cn.zswltech.mithras.service.service.lib.payment.handler.PaymentBaseInfoLibHandler;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2022/8/22 11:25
 */
@Service("paymentBaseInfo")
public class PaymentBaseInfoFactory implements EditdataCompareFactory {
    @Resource
    private PaymentBaseInfoLibMapper libMapper;
    @Resource
    private PaymentBaseInfoLibHandler handler;
    @Resource
    private CommonVersionMapper commonVersionMapper;

    @Override
    public AbstractDataCompare createCompare(List rsps, String version) {
        return new DefaultDataCompare<PaymentBaseInfo, PaymentBaseInfoLib, PaymentDetailRsp>(rsps, libMapper, handler, commonVersionMapper, BusinessModuleEnum.PAYMENT.name(), version);
    }
}
