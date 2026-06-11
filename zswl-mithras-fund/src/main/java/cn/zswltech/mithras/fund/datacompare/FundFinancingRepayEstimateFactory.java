package cn.zswltech.mithras.fund.datacompare;

import cn.zswltech.mithras.dto.fund.financing.repay.FundFinancingRepayEstimateListRSP;
import cn.zswltech.mithras.foundation.persistence.mapper.CommonVersionMapper;
import cn.zswltech.mithras.fund.mapper.lib.financing.FundFinancingRepayEstimateLibMapper;
import cn.zswltech.mithras.fund.model.financing.FundFinancingRepayEstimate;
import cn.zswltech.mithras.fund.model.financing.FundFinancingRepayEstimateLib;
import cn.zswltech.mithras.foundation.datacompare.AbstractDataCompare;
import cn.zswltech.mithras.foundation.datacompare.EditdataCompareFactory;
import cn.zswltech.mithras.foundation.datacompare.compare.DefaultDataCompare;
import cn.zswltech.mithras.fund.versioning.financing.handler.impl.FundFinancingRepayEstimateLibHandler;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @create: 2022-08-27
 **/
@Service("fundFinancingRepayEstimate")
public class FundFinancingRepayEstimateFactory implements EditdataCompareFactory {
    @Resource
    private FundFinancingRepayEstimateLibMapper libMapper;
    @Resource
    private FundFinancingRepayEstimateLibHandler handler;
    @Resource
    private CommonVersionMapper commonVersionMapper;

    @Override
    public AbstractDataCompare createCompare(List rsps, String version) {
        return new DefaultDataCompare<FundFinancingRepayEstimate, FundFinancingRepayEstimateLib, FundFinancingRepayEstimateListRSP>(rsps, libMapper,
                handler,
                commonVersionMapper, "FUND_FINANCING", version);
    }
}