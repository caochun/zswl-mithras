package cn.zswltech.mithras.fund.application.lib.receiptrepay.handler.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.zswltech.mithras.dto.fund.receiptrepay.FundRepayAccountListRSP;
import cn.zswltech.mithras.fund.enums.receiptrepay.FundReceiptRepayInfoModule;
import cn.zswltech.mithras.fund.model.receiptrepay.FundRepayAccount;
import cn.zswltech.mithras.fund.model.receiptrepay.FundRepayAccountLib;
import cn.zswltech.mithras.fund.application.lib.receiptrepay.handler.AbstractFundReceiptRepayLibHandler;
import org.springframework.stereotype.Component;

/**
 * 还款账户
 *
 * @author wangchuanhao
 * @date 2023/2/20 4:03 PM
 */
@Component
public class FundRepayAccountLibHandler extends AbstractFundReceiptRepayLibHandler<FundRepayAccountLib, FundRepayAccount, FundRepayAccountListRSP> {

    @Override
    protected FundRepayAccountLib entity2Lib(FundRepayAccount f) {
        return BeanUtil.copyProperties(f, FundRepayAccountLib.class);
    }

    @Override
    protected FundRepayAccount lib2Entity(FundRepayAccountLib t) {
        return BeanUtil.copyProperties(t, FundRepayAccount.class);
    }

    @Override
    protected FundRepayAccountListRSP lib2Rsp(FundRepayAccountLib f) {
        return BeanUtil.copyProperties(f, FundRepayAccountListRSP.class);
    }

    @Override
    public FundReceiptRepayInfoModule getSubModule() {
        return FundReceiptRepayInfoModule.REPAY_ACCOUNT;
    }

}
