package cn.zswltech.mithras.fund.datacompare;

import cn.zswltech.mithras.dto.fund.receiptrepay.FundReceiptRepayCashDepositListRSP;
import cn.zswltech.mithras.foundation.persistence.mapper.CommonVersionMapper;
import cn.zswltech.mithras.fund.persistence.mapper.lib.receiptrepay.FundReceiptRepayCashDepositLibMapper;
import cn.zswltech.mithras.fund.persistence.model.receiptrepay.FundReceiptRepayCashDeposit;
import cn.zswltech.mithras.fund.persistence.model.receiptrepay.FundReceiptRepayCashDepositLib;
import cn.zswltech.mithras.foundation.datacompare.AbstractDataCompare;
import cn.zswltech.mithras.foundation.datacompare.EditdataCompareFactory;
import cn.zswltech.mithras.foundation.datacompare.compare.DefaultDataCompare;
import cn.zswltech.mithras.fund.versioning.receiptrepay.handler.impl.FundReceiptRepayCashDepositLibHandler;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author wangchuanhao
 * @date 2023/2/22
 * @description
 */
@Component("fundReceiptRepayCashDeposit")
public class FundReceiptRepayCashDepositFactory implements EditdataCompareFactory {
    @Resource
    private FundReceiptRepayCashDepositLibMapper libMapper;
    @Resource
    private FundReceiptRepayCashDepositLibHandler handler;
    @Resource
    private CommonVersionMapper commonVersionMapper;

    @Override
    public AbstractDataCompare createCompare(List rsps, String version) {
        return new DefaultDataCompare<FundReceiptRepayCashDeposit, FundReceiptRepayCashDepositLib, FundReceiptRepayCashDepositListRSP>(rsps, libMapper, handler,commonVersionMapper, "FUND_RECEIPT_REPAY", version);
    }
}
