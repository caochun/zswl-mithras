package cn.zswltech.mithras.service.service.lib.fund.receiptrepay.handler.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.ListUtil;
import cn.zswltech.mithras.dto.fund.receiptrepay.FundReceiptAccountListRSP;
import cn.zswltech.mithras.service.convert.fund.receiptrepay.FundReceiptAccountConverter;
import cn.zswltech.mithras.service.enums.fund.receiptrepay.FundReceiptRepayInfoModule;
import cn.zswltech.mithras.service.mapper.model.fund.receiptrepay.FundReceiptAccount;
import cn.zswltech.mithras.service.mapper.model.fund.receiptrepay.FundReceiptAccountLib;
import cn.zswltech.mithras.service.service.lib.fund.receiptrepay.handler.AbstractFundReceiptRepayLibHandler;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;


/**
 * 对方收款账户
 *
 * @author wangchuanhao
 * @date 2023/2/20 4:03 PM
 */
@Component
public class FundReceiptAccountLibHandler extends AbstractFundReceiptRepayLibHandler<FundReceiptAccountLib, FundReceiptAccount, FundReceiptAccountListRSP> {

    @Resource
    private FundReceiptAccountConverter baseConvert;

    @Override
    protected FundReceiptAccountLib entity2Lib(FundReceiptAccount f) {
        return BeanUtil.copyProperties(f, FundReceiptAccountLib.class);
    }

    @Override
    protected FundReceiptAccount lib2Entity(FundReceiptAccountLib t) {
        return BeanUtil.copyProperties(t, FundReceiptAccount.class);
    }

    @Override
    protected FundReceiptAccountListRSP lib2Rsp(FundReceiptAccountLib f) {
        return baseConvert.lib2ListRsp(f);
    }

    @Override
    public FundReceiptRepayInfoModule getSubModule() {
        return FundReceiptRepayInfoModule.RECEIPT_ACCOUNT;
    }


}
