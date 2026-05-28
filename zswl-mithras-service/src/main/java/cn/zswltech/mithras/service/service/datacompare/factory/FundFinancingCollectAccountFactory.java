package cn.zswltech.mithras.service.service.datacompare.factory;

import cn.zswltech.mithras.dto.fund.financing.collectaccount.FundFinancingCollectAccountListRSP;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.mapper.lib.CommonVersionMapper;
import cn.zswltech.mithras.service.mapper.lib.fund.financing.FundFinancingCollectAccountLibMapper;
import cn.zswltech.mithras.service.mapper.model.fund.financing.FundFinancingCollectAccount;
import cn.zswltech.mithras.service.mapper.model.fund.financing.FundFinancingCollectAccountLib;
import cn.zswltech.mithras.service.service.datacompare.AbstractDataCompare;
import cn.zswltech.mithras.service.service.datacompare.EditdataCompareFactory;
import cn.zswltech.mithras.service.service.datacompare.compare.DefaultDataCompare;
import cn.zswltech.mithras.service.service.lib.fund.financing.handler.impl.FundFinancingCollectAccountLibHandler;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @create: 2022-08-27
 **/
@Service("fundFinancingCollectAccount")
public class FundFinancingCollectAccountFactory implements EditdataCompareFactory {
    @Resource
    private FundFinancingCollectAccountLibMapper libMapper;
    @Resource
    private FundFinancingCollectAccountLibHandler handler;
    @Resource
    private CommonVersionMapper commonVersionMapper;

    @Override
    public AbstractDataCompare createCompare(List rsps, String version) {
        return new DefaultDataCompare<FundFinancingCollectAccount, FundFinancingCollectAccountLib, FundFinancingCollectAccountListRSP>(rsps, libMapper
                , handler,
                commonVersionMapper, BusinessModuleEnum.FUND_FINANCING.name(), version);
    }
}