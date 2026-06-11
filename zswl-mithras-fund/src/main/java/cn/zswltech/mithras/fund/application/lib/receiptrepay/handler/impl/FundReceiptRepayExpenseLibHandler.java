package cn.zswltech.mithras.fund.application.lib.receiptrepay.handler.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.zswltech.mithras.dto.fund.receiptrepay.FundReceiptRepayExpenseListRSP;
import cn.zswltech.mithras.fund.enums.receiptrepay.FundReceiptRepayInfoModule;
import cn.zswltech.mithras.fund.mapper.model.receiptrepay.FundReceiptRepayExpense;
import cn.zswltech.mithras.fund.mapper.model.receiptrepay.FundReceiptRepayExpenseLib;
import cn.zswltech.mithras.fund.application.lib.receiptrepay.handler.AbstractFundReceiptRepayLibHandler;
import org.springframework.stereotype.Component;

/**
 * 费用一览表
 *
 * @author wangchuanhao
 * @date 2023/2/20 3:52 PM
 */
@Component
public class FundReceiptRepayExpenseLibHandler extends AbstractFundReceiptRepayLibHandler<FundReceiptRepayExpenseLib, FundReceiptRepayExpense, FundReceiptRepayExpenseListRSP> {

    @Override
    protected FundReceiptRepayExpenseLib entity2Lib(FundReceiptRepayExpense f) {
        return BeanUtil.copyProperties(f, FundReceiptRepayExpenseLib.class);
    }

    @Override
    protected FundReceiptRepayExpense lib2Entity(FundReceiptRepayExpenseLib t) {
        return BeanUtil.copyProperties(t, FundReceiptRepayExpense.class);
    }

    @Override
    protected FundReceiptRepayExpenseListRSP lib2Rsp(FundReceiptRepayExpenseLib f) {
        return BeanUtil.copyProperties(f, FundReceiptRepayExpenseListRSP.class);
    }

    @Override
    public FundReceiptRepayInfoModule getSubModule() {
        return FundReceiptRepayInfoModule.EXPENSE;
    }

}
