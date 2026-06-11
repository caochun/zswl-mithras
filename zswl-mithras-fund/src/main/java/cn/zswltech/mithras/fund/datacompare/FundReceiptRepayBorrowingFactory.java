package cn.zswltech.mithras.fund.datacompare;

import cn.zswltech.mithras.dto.fund.receiptrepay.FundReceiptRepayBorrowingListRSP;
import cn.zswltech.mithras.foundation.persistence.mapper.CommonVersionMapper;
import cn.zswltech.mithras.fund.mapper.lib.receiptrepay.FundReceiptRepayBorrowingLibMapper;
import cn.zswltech.mithras.fund.mapper.model.receiptrepay.FundReceiptRepayBorrowing;
import cn.zswltech.mithras.fund.mapper.model.receiptrepay.FundReceiptRepayBorrowingLib;
import cn.zswltech.mithras.foundation.datacompare.AbstractDataCompare;
import cn.zswltech.mithras.foundation.datacompare.EditdataCompareFactory;
import cn.zswltech.mithras.foundation.datacompare.compare.DefaultDataCompare;
import cn.zswltech.mithras.fund.application.lib.receiptrepay.handler.impl.FundReceiptRepayBorrowingLibHandler;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author wangchuanhao
 * @date 2023/2/22
 * @description
 */
@Component("fundReceiptRepayBorrowing")
public class FundReceiptRepayBorrowingFactory implements EditdataCompareFactory {
    @Resource
    private FundReceiptRepayBorrowingLibMapper libMapper;
    @Resource
    private FundReceiptRepayBorrowingLibHandler handler;
    @Resource
    private CommonVersionMapper commonVersionMapper;

    @Override
    public AbstractDataCompare createCompare(List rsps, String version) {
        return new DefaultDataCompare<FundReceiptRepayBorrowing, FundReceiptRepayBorrowingLib, FundReceiptRepayBorrowingListRSP>(rsps, libMapper, handler,commonVersionMapper, "FUND_RECEIPT_REPAY", version);
    }
}
