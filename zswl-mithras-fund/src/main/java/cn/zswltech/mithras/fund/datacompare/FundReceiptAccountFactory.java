package cn.zswltech.mithras.fund.datacompare;

import cn.zswltech.mithras.dto.fund.receiptrepay.FundReceiptAccountListRSP;
import cn.zswltech.mithras.foundation.persistence.mapper.CommonVersionMapper;
import cn.zswltech.mithras.fund.persistence.mapper.lib.receiptrepay.FundReceiptAccountLibMapper;
import cn.zswltech.mithras.fund.persistence.model.receiptrepay.FundReceiptAccount;
import cn.zswltech.mithras.fund.persistence.model.receiptrepay.FundReceiptAccountLib;
import cn.zswltech.mithras.foundation.datacompare.AbstractDataCompare;
import cn.zswltech.mithras.foundation.datacompare.EditdataCompareFactory;
import cn.zswltech.mithras.foundation.datacompare.compare.DefaultDataCompare;
import cn.zswltech.mithras.fund.versioning.receiptrepay.handler.impl.FundReceiptAccountLibHandler;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author wangchuanhao
 * @date 2023/2/22
 * @description
 */
@Component("fundReceiptRepayReceiptAccount")
public class FundReceiptAccountFactory implements EditdataCompareFactory {
    @Resource
    private FundReceiptAccountLibMapper libMapper;
    @Resource
    private FundReceiptAccountLibHandler handler;
    @Resource
    private CommonVersionMapper commonVersionMapper;

    @Override
    public AbstractDataCompare createCompare(List rsps, String version) {
        return new DefaultDataCompare<FundReceiptAccount, FundReceiptAccountLib, FundReceiptAccountListRSP>(rsps, libMapper, handler,commonVersionMapper, "FUND_RECEIPT_REPAY", version);
    }
}
