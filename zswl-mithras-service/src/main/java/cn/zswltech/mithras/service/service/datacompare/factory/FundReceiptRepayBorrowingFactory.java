package cn.zswltech.mithras.service.service.datacompare.factory;

import cn.zswltech.mithras.dto.fund.receiptrepay.FundReceiptRepayBorrowingListRSP;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.mapper.lib.CommonVersionMapper;
import cn.zswltech.mithras.service.mapper.lib.fund.receiptrepay.FundReceiptRepayBorrowingLibMapper;
import cn.zswltech.mithras.service.mapper.model.fund.receiptrepay.FundReceiptRepayBorrowing;
import cn.zswltech.mithras.service.mapper.model.fund.receiptrepay.FundReceiptRepayBorrowingLib;
import cn.zswltech.mithras.service.service.datacompare.AbstractDataCompare;
import cn.zswltech.mithras.service.service.datacompare.EditdataCompareFactory;
import cn.zswltech.mithras.service.service.datacompare.compare.DefaultDataCompare;
import cn.zswltech.mithras.service.service.lib.fund.receiptrepay.handler.impl.FundReceiptRepayBorrowingLibHandler;
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
        return new DefaultDataCompare<FundReceiptRepayBorrowing, FundReceiptRepayBorrowingLib, FundReceiptRepayBorrowingListRSP>(rsps, libMapper, handler,commonVersionMapper, BusinessModuleEnum.FUND_RECEIPT_REPAY.name(), version);
    }
}
