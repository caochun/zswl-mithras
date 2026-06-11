package cn.zswltech.mithras.fund.datacompare;

import cn.zswltech.mithras.dto.fund.financing.payaccount.FundFinancingPayAccountListRSP;
import cn.zswltech.mithras.foundation.persistence.mapper.CommonVersionMapper;
import cn.zswltech.mithras.fund.mapper.lib.financing.FundFinancingPayAccountLibMapper;
import cn.zswltech.mithras.fund.mapper.model.financing.FundFinancingPayAccount;
import cn.zswltech.mithras.fund.mapper.model.financing.FundFinancingPayAccountLib;
import cn.zswltech.mithras.foundation.datacompare.AbstractDataCompare;
import cn.zswltech.mithras.foundation.datacompare.EditdataCompareFactory;
import cn.zswltech.mithras.foundation.datacompare.compare.DefaultDataCompare;
import cn.zswltech.mithras.fund.application.lib.financing.handler.impl.FundFinancingPayAccountLibHandler;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @create: 2022-08-27
 **/
@Service("fundFinancingPayAccount")
public class FundFinancingPayAccountFactory implements EditdataCompareFactory {
    @Resource
    private FundFinancingPayAccountLibMapper libMapper;
    @Resource
    private FundFinancingPayAccountLibHandler handler;
    @Resource
    private CommonVersionMapper commonVersionMapper;

    @Override
    public AbstractDataCompare createCompare(List rsps, String version) {
        return new DefaultDataCompare<FundFinancingPayAccount, FundFinancingPayAccountLib, FundFinancingPayAccountListRSP>(rsps, libMapper, handler,
                commonVersionMapper, "FUND_FINANCING", version);
    }
}