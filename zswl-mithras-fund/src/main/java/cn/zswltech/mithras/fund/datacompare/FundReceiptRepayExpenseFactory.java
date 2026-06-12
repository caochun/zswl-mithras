package cn.zswltech.mithras.fund.datacompare;

import cn.zswltech.mithras.dto.fund.receiptrepay.FundReceiptRepayExpenseListRSP;
import cn.zswltech.mithras.foundation.persistence.mapper.CommonVersionMapper;
import cn.zswltech.mithras.fund.persistence.mapper.lib.receiptrepay.FundReceiptRepayExpenseLibMapper;
import cn.zswltech.mithras.fund.persistence.model.receiptrepay.FundReceiptRepayExpense;
import cn.zswltech.mithras.fund.persistence.model.receiptrepay.FundReceiptRepayExpenseLib;
import cn.zswltech.mithras.foundation.datacompare.AbstractDataCompare;
import cn.zswltech.mithras.foundation.datacompare.EditdataCompareFactory;
import cn.zswltech.mithras.foundation.datacompare.compare.DefaultDataCompare;
import cn.zswltech.mithras.fund.versioning.receiptrepay.handler.impl.FundReceiptRepayExpenseLibHandler;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author wangchuanhao
 * @date 2023/2/22
 * @description
 */
@Component("fundReceiptRepayExpense")
public class FundReceiptRepayExpenseFactory implements EditdataCompareFactory {
    @Resource
    private FundReceiptRepayExpenseLibMapper libMapper;
    @Resource
    private FundReceiptRepayExpenseLibHandler handler;
    @Resource
    private CommonVersionMapper commonVersionMapper;

    @Override
    public AbstractDataCompare createCompare(List rsps, String version) {
        return new DefaultDataCompare<FundReceiptRepayExpense, FundReceiptRepayExpenseLib, FundReceiptRepayExpenseListRSP>(rsps, libMapper, handler,commonVersionMapper, "FUND_RECEIPT_REPAY", version);
    }
}
