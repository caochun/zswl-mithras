package cn.zswltech.mithras.service.service.datacompare.factory;

import cn.zswltech.mithras.dto.fund.financing.pledge.FundFinancingPledgeListRSP;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.mapper.lib.CommonVersionMapper;
import cn.zswltech.mithras.fund.infrastructure.persistence.mapper.lib.financing.FundFinancingPledgeInfoLibMapper;
import cn.zswltech.mithras.fund.infrastructure.persistence.mapper.model.financing.FundFinancingPledgeInfo;
import cn.zswltech.mithras.fund.infrastructure.persistence.mapper.model.financing.FundFinancingPledgeInfoLib;
import cn.zswltech.mithras.service.service.datacompare.AbstractDataCompare;
import cn.zswltech.mithras.service.service.datacompare.EditdataCompareFactory;
import cn.zswltech.mithras.service.service.datacompare.compare.DefaultDataCompare;
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
                commonVersionMapper, BusinessModuleEnum.FUND_FINANCING.name(), version);
    }
}