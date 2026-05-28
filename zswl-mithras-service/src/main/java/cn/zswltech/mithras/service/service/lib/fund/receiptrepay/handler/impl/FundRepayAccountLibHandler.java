package cn.zswltech.mithras.service.service.lib.fund.receiptrepay.handler.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.ListUtil;
import cn.zswltech.mithras.dto.fund.receiptrepay.FundRepayAccountListRSP;
import cn.zswltech.mithras.service.convert.fund.receiptrepay.FundRepayAccountConverter;
import cn.zswltech.mithras.service.enums.fund.receiptrepay.FundReceiptRepayInfoModule;
import cn.zswltech.mithras.service.mapper.model.fund.receiptrepay.FundRepayAccount;
import cn.zswltech.mithras.service.mapper.model.fund.receiptrepay.FundRepayAccountLib;
import cn.zswltech.mithras.service.service.lib.fund.receiptrepay.handler.AbstractFundReceiptRepayLibHandler;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 还款账户
 *
 * @author wangchuanhao
 * @date 2023/2/20 4:03 PM
 */
@Component
public class FundRepayAccountLibHandler extends AbstractFundReceiptRepayLibHandler<FundRepayAccountLib, FundRepayAccount, FundRepayAccountListRSP> {

    @Resource
    private FundRepayAccountConverter baseConvert;

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
        return baseConvert.lib2ListRsp(f);
    }

    @Override
    public FundReceiptRepayInfoModule getSubModule() {
        return FundReceiptRepayInfoModule.REPAY_ACCOUNT;
    }

}
