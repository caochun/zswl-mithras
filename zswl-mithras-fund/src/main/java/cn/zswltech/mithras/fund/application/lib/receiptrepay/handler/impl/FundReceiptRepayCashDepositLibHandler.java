package cn.zswltech.mithras.fund.application.lib.receiptrepay.handler.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.zswltech.mithras.dto.fund.receiptrepay.FundReceiptRepayCashDepositListRSP;
import cn.zswltech.mithras.fund.domain.enums.receiptrepay.FundReceiptRepayInfoModule;
import cn.zswltech.mithras.fund.infrastructure.persistence.mapper.model.receiptrepay.FundReceiptRepayCashDeposit;
import cn.zswltech.mithras.fund.infrastructure.persistence.mapper.model.receiptrepay.FundReceiptRepayCashDepositLib;
import cn.zswltech.mithras.fund.application.lib.receiptrepay.handler.AbstractFundReceiptRepayLibHandler;
import org.springframework.stereotype.Component;

/**
 * 保证金明细
 *
 * @author wangchuanhao
 * @date 2023/2/20 3:59 PM
 */
@Component
public class FundReceiptRepayCashDepositLibHandler extends AbstractFundReceiptRepayLibHandler<FundReceiptRepayCashDepositLib, FundReceiptRepayCashDeposit, FundReceiptRepayCashDepositListRSP> {

    @Override
    protected FundReceiptRepayCashDepositLib entity2Lib(FundReceiptRepayCashDeposit f) {
        return BeanUtil.copyProperties(f, FundReceiptRepayCashDepositLib.class);
    }

    @Override
    protected FundReceiptRepayCashDeposit lib2Entity(FundReceiptRepayCashDepositLib t) {
        return BeanUtil.copyProperties(t, FundReceiptRepayCashDeposit.class);
    }

    @Override
    protected FundReceiptRepayCashDepositListRSP lib2Rsp(FundReceiptRepayCashDepositLib f) {
        return BeanUtil.copyProperties(f, FundReceiptRepayCashDepositListRSP.class);
    }

    @Override
    public FundReceiptRepayInfoModule getSubModule() {
        return FundReceiptRepayInfoModule.CASH_DEPOSIT;
    }

}
