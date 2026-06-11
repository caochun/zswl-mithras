package cn.zswltech.mithras.fund.datacompare;

import cn.zswltech.mithras.dto.fund.financing.pledge.FundFinancingPledgeListRSP;
import cn.zswltech.mithras.foundation.persistence.mapper.CommonVersionMapper;
import cn.zswltech.mithras.fund.mapper.lib.financing.FundFinancingPledgeInfoLibMapper;
import cn.zswltech.mithras.fund.model.financing.FundFinancingPledgeInfo;
import cn.zswltech.mithras.fund.model.financing.FundFinancingPledgeInfoLib;
import cn.zswltech.mithras.foundation.datacompare.AbstractDataCompare;
import cn.zswltech.mithras.foundation.datacompare.EditdataCompareFactory;
import cn.zswltech.mithras.foundation.datacompare.compare.DefaultDataCompare;
import cn.zswltech.mithras.fund.application.lib.financing.handler.impl.FundFinancingPledgeInfoLibHandler;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @create: 2022-08-27
 **/
@Service("fundFinancingPledge")
public class FundFinancingPledgeFactory implements EditdataCompareFactory {
    @Resource
    private FundFinancingPledgeInfoLibMapper libMapper;
    @Resource
    private FundFinancingPledgeInfoLibHandler handler;
    @Resource
    private CommonVersionMapper commonVersionMapper;

    @Override
    public AbstractDataCompare createCompare(List rsps, String version) {
        return new DefaultDataCompare<FundFinancingPledgeInfo, FundFinancingPledgeInfoLib, FundFinancingPledgeListRSP>(rsps, libMapper, handler,
                commonVersionMapper, "FUND_FINANCING", version);
    }
}