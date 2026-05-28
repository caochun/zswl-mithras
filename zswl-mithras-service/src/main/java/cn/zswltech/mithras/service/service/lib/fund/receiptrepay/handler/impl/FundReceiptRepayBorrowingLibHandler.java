package cn.zswltech.mithras.service.service.lib.fund.receiptrepay.handler.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.ListUtil;
import cn.zswltech.mithras.dto.fund.receiptrepay.FundReceiptRepayBaseInfoDetailRSP;
import cn.zswltech.mithras.dto.fund.receiptrepay.FundReceiptRepayBorrowingListRSP;
import cn.zswltech.mithras.service.convert.fund.receiptrepay.FundReceiptRepayBorrowingConverter;
import cn.zswltech.mithras.service.enums.fund.receiptrepay.FundReceiptRepayInfoModule;
import cn.zswltech.mithras.service.mapper.model.fund.receiptrepay.FundReceiptRepayBaseInfo;
import cn.zswltech.mithras.service.mapper.model.fund.receiptrepay.FundReceiptRepayBaseInfoLib;
import cn.zswltech.mithras.service.mapper.model.fund.receiptrepay.FundReceiptRepayBorrowing;
import cn.zswltech.mithras.service.mapper.model.fund.receiptrepay.FundReceiptRepayBorrowingLib;
import cn.zswltech.mithras.service.service.lib.fund.receiptrepay.handler.AbstractFundReceiptRepayLibHandler;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 借款流入
 *
 * @author wangchuanhao
 * @date 2023/2/20 3:42 PM
 */
@Component
public class FundReceiptRepayBorrowingLibHandler extends AbstractFundReceiptRepayLibHandler<FundReceiptRepayBorrowingLib, FundReceiptRepayBorrowing, FundReceiptRepayBorrowingListRSP> {

    @Resource
    private FundReceiptRepayBorrowingConverter baseConverter;

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
        return baseConverter.lib2ListRsp(f);
    }

    @Override
    public FundReceiptRepayInfoModule getSubModule() {
        return FundReceiptRepayInfoModule.BORROWING;
    }

}
