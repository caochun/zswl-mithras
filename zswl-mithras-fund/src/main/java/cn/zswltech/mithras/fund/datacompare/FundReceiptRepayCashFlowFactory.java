package cn.zswltech.mithras.fund.datacompare;

import cn.zswltech.mithras.dto.fund.receiptrepay.FundReceiptRepayCashFlowListRSP;
import cn.zswltech.mithras.foundation.persistence.mapper.CommonVersionMapper;
import cn.zswltech.mithras.fund.mapper.lib.receiptrepay.FundReceiptRepayCashFlowLibMapper;
import cn.zswltech.mithras.fund.model.receiptrepay.FundReceiptRepayCashFlow;
import cn.zswltech.mithras.fund.model.receiptrepay.FundReceiptRepayCashFlowLib;
import cn.zswltech.mithras.foundation.datacompare.AbstractDataCompare;
import cn.zswltech.mithras.foundation.datacompare.EditdataCompareFactory;
import cn.zswltech.mithras.foundation.datacompare.compare.DefaultDataCompare;
import cn.zswltech.mithras.fund.application.lib.receiptrepay.handler.impl.FundReceiptRepayCashFlowLibHandler;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author wangchuanhao
 * @date 2023/2/22
 * @description
 */
@Component("fundReceiptRepayCashFlow")
public class FundReceiptRepayCashFlowFactory implements EditdataCompareFactory {
    @Resource
    private FundReceiptRepayCashFlowLibMapper libMapper;
    @Resource
    private FundReceiptRepayCashFlowLibHandler handler;
    @Resource
    private CommonVersionMapper commonVersionMapper;

    @Override
    public AbstractDataCompare createCompare(List rsps, String version) {
        return new DefaultDataCompare<FundReceiptRepayCashFlow, FundReceiptRepayCashFlowLib, FundReceiptRepayCashFlowListRSP>(rsps, libMapper, handler,commonVersionMapper, "FUND_RECEIPT_REPAY", version);
    }
}
