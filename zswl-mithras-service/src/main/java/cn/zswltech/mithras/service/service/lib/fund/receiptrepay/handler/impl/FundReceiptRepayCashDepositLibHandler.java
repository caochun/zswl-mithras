package cn.zswltech.mithras.service.service.lib.fund.receiptrepay.handler.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.ListUtil;
import cn.zswltech.mithras.dto.fund.receiptrepay.FundReceiptRepayCashDepositListRSP;
import cn.zswltech.mithras.service.convert.fund.receiptrepay.FundReceiptRepayCashDepositConverter;
import cn.zswltech.mithras.service.enums.fund.receiptrepay.FundReceiptRepayInfoModule;
import cn.zswltech.mithras.service.mapper.model.fund.receiptrepay.FundReceiptRepayCashDeposit;
import cn.zswltech.mithras.service.mapper.model.fund.receiptrepay.FundReceiptRepayCashDepositLib;
import cn.zswltech.mithras.service.service.lib.fund.receiptrepay.handler.AbstractFundReceiptRepayLibHandler;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 保证金明细
 *
 * @author wangchuanhao
 * @date 2023/2/20 3:59 PM
 */
@Component
public class FundReceiptRepayCashDepositLibHandler extends AbstractFundReceiptRepayLibHandler<FundReceiptRepayCashDepositLib, FundReceiptRepayCashDeposit, FundReceiptRepayCashDepositListRSP> {

    @Resource
    private FundReceiptRepayCashDepositConverter baseConverter;

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
        return baseConverter.lib2ListRsp(f);
    }

    @Override
    public FundReceiptRepayInfoModule getSubModule() {
        return FundReceiptRepayInfoModule.CASH_DEPOSIT;
    }

}
