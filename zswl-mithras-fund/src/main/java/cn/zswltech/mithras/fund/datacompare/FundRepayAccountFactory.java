package cn.zswltech.mithras.fund.datacompare;

import cn.zswltech.mithras.dto.fund.receiptrepay.FundRepayAccountListRSP;
import cn.zswltech.mithras.foundation.persistence.mapper.CommonVersionMapper;
import cn.zswltech.mithras.fund.persistence.mapper.lib.receiptrepay.FundRepayAccountLibMapper;
import cn.zswltech.mithras.fund.persistence.model.receiptrepay.FundRepayAccount;
import cn.zswltech.mithras.fund.persistence.model.receiptrepay.FundRepayAccountLib;
import cn.zswltech.mithras.foundation.datacompare.AbstractDataCompare;
import cn.zswltech.mithras.foundation.datacompare.EditdataCompareFactory;
import cn.zswltech.mithras.foundation.datacompare.compare.DefaultDataCompare;
import cn.zswltech.mithras.fund.versioning.receiptrepay.handler.impl.FundRepayAccountLibHandler;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author wangchuanhao
 * @date 2023/2/22
 * @description
 */
@Component("fundReceiptRepayRepayAccount")
public class FundRepayAccountFactory implements EditdataCompareFactory {
    @Resource
    private FundRepayAccountLibMapper libMapper;
    @Resource
    private FundRepayAccountLibHandler handler;
    @Resource
    private CommonVersionMapper commonVersionMapper;

    @Override
    public AbstractDataCompare createCompare(List rsps, String version) {
        return new DefaultDataCompare<FundRepayAccount, FundRepayAccountLib, FundRepayAccountListRSP>(rsps, libMapper, handler,commonVersionMapper, "FUND_RECEIPT_REPAY", version);
    }
}
