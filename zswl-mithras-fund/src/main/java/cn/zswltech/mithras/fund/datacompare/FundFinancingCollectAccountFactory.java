package cn.zswltech.mithras.fund.datacompare;

import cn.zswltech.mithras.dto.fund.financing.collectaccount.FundFinancingCollectAccountListRSP;
import cn.zswltech.mithras.foundation.persistence.mapper.CommonVersionMapper;
import cn.zswltech.mithras.fund.mapper.lib.financing.FundFinancingCollectAccountLibMapper;
import cn.zswltech.mithras.fund.model.financing.FundFinancingCollectAccount;
import cn.zswltech.mithras.fund.model.financing.FundFinancingCollectAccountLib;
import cn.zswltech.mithras.foundation.datacompare.AbstractDataCompare;
import cn.zswltech.mithras.foundation.datacompare.EditdataCompareFactory;
import cn.zswltech.mithras.foundation.datacompare.compare.DefaultDataCompare;
import cn.zswltech.mithras.fund.versioning.financing.handler.impl.FundFinancingCollectAccountLibHandler;
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
                commonVersionMapper, "FUND_FINANCING", version);
    }
}