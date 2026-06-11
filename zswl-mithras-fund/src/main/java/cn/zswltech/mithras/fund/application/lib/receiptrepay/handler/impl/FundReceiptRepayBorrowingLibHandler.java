package cn.zswltech.mithras.fund.application.lib.receiptrepay.handler.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.zswltech.mithras.dto.fund.receiptrepay.FundReceiptRepayBorrowingListRSP;
import cn.zswltech.mithras.fund.enums.receiptrepay.FundReceiptRepayInfoModule;
import cn.zswltech.mithras.fund.mapper.model.receiptrepay.FundReceiptRepayBorrowing;
import cn.zswltech.mithras.fund.mapper.model.receiptrepay.FundReceiptRepayBorrowingLib;
import cn.zswltech.mithras.fund.application.lib.receiptrepay.handler.AbstractFundReceiptRepayLibHandler;
import org.springframework.stereotype.Component;

/**
 * 借款流入
 *
 * @author wangchuanhao
 * @date 2023/2/20 3:42 PM
 */
@Component
public class FundReceiptRepayBorrowingLibHandler extends AbstractFundReceiptRepayLibHandler<FundReceiptRepayBorrowingLib, FundReceiptRepayBorrowing, FundReceiptRepayBorrowingListRSP> {

    @Override
    protected FundReceiptRepayBorrowingLib entity2Lib(FundReceiptRepayBorrowing f) {
        return BeanUtil.copyProperties(f, FundReceiptRepayBorrowingLib.class);
    }

    @Override
    protected FundReceiptRepayBorrowing lib2Entity(FundReceiptRepayBorrowingLib t) {
        return BeanUtil.copyProperties(t, FundReceiptRepayBorrowing.class);
    }

    @Override
    protected FundReceiptRepayBorrowingListRSP lib2Rsp(FundReceiptRepayBorrowingLib f) {
        return BeanUtil.copyProperties(f, FundReceiptRepayBorrowingListRSP.class);
    }

    @Override
    public FundReceiptRepayInfoModule getSubModule() {
        return FundReceiptRepayInfoModule.BORROWING;
    }

}
