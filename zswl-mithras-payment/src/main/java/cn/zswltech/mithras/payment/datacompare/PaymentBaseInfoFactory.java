package cn.zswltech.mithras.payment.datacompare;

import cn.zswltech.mithras.api.payment.dto.PaymentDetailRsp;
import cn.zswltech.mithras.foundation.persistence.mapper.CommonVersionMapper;
import cn.zswltech.mithras.payment.mapper.lib.PaymentBaseInfoLibMapper;
import cn.zswltech.mithras.payment.model.PaymentBaseInfo;
import cn.zswltech.mithras.payment.model.PaymentBaseInfoLib;
import cn.zswltech.mithras.foundation.datacompare.AbstractDataCompare;
import cn.zswltech.mithras.foundation.datacompare.EditdataCompareFactory;
import cn.zswltech.mithras.foundation.datacompare.compare.DefaultDataCompare;
import cn.zswltech.mithras.payment.application.lib.handler.PaymentBaseInfoLibHandler;
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
        return new DefaultDataCompare<PaymentBaseInfo, PaymentBaseInfoLib, PaymentDetailRsp>(rsps, libMapper, handler, commonVersionMapper, "PAYMENT", version);
    }
}
