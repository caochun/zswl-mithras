package cn.zswltech.mithras.fund.application.lib.receiptrepay.handler.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.zswltech.mithras.dto.fund.receiptrepay.FundReceiptAccountListRSP;
import cn.zswltech.mithras.fund.enums.receiptrepay.FundReceiptRepayInfoModule;
import cn.zswltech.mithras.fund.model.receiptrepay.FundReceiptAccount;
import cn.zswltech.mithras.fund.model.receiptrepay.FundReceiptAccountLib;
import cn.zswltech.mithras.fund.application.lib.receiptrepay.handler.AbstractFundReceiptRepayLibHandler;
import org.springframework.stereotype.Component;

/**
 * 对方收款账户
 *
 * @author wangchuanhao
 * @date 2023/2/20 4:03 PM
 */
@Component
public class FundReceiptAccountLibHandler extends AbstractFundReceiptRepayLibHandler<FundReceiptAccountLib, FundReceiptAccount, FundReceiptAccountListRSP> {

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
        return BeanUtil.copyProperties(f, FundReceiptAccountListRSP.class);
    }

    @Override
    public FundReceiptRepayInfoModule getSubModule() {
        return FundReceiptRepayInfoModule.RECEIPT_ACCOUNT;
    }


}
