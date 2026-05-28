package cn.zswltech.mithras.service.service.lib.fund.receiptrepay.handler.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.ListUtil;
import cn.zswltech.mithras.dto.fund.receiptrepay.FundReceiptRepayExpenseListRSP;
import cn.zswltech.mithras.service.convert.fund.receiptrepay.FundReceiptRepayExpenseConverter;
import cn.zswltech.mithras.service.enums.fund.receiptrepay.FundReceiptRepayInfoModule;
import cn.zswltech.mithras.service.mapper.model.fund.receiptrepay.FundReceiptRepayExpense;
import cn.zswltech.mithras.service.mapper.model.fund.receiptrepay.FundReceiptRepayExpenseLib;
import cn.zswltech.mithras.service.service.lib.fund.receiptrepay.handler.AbstractFundReceiptRepayLibHandler;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 费用一览表
 *
 * @author wangchuanhao
 * @date 2023/2/20 3:52 PM
 */
@Component
public class FundReceiptRepayExpenseLibHandler extends AbstractFundReceiptRepayLibHandler<FundReceiptRepayExpenseLib, FundReceiptRepayExpense, FundReceiptRepayExpenseListRSP> {

    @Resource
    private FundReceiptRepayExpenseConverter baseConverter;

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
        return baseConverter.lib2ListRsp(f);
    }

    @Override
    public FundReceiptRepayInfoModule getSubModule() {
        return FundReceiptRepayInfoModule.EXPENSE;
    }

}
