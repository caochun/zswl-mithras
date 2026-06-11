package cn.zswltech.mithras.fund.datacompare;

import cn.zswltech.mithras.dto.fund.financing.plan.FundFinancingPlanDetailRSP;
import cn.zswltech.mithras.foundation.persistence.mapper.CommonVersionMapper;
import cn.zswltech.mithras.fund.mapper.lib.financing.FundFinancingPlanLibMapper;
import cn.zswltech.mithras.fund.model.financing.FundFinancingPlan;
import cn.zswltech.mithras.fund.model.financing.FundFinancingPlanLib;
import cn.zswltech.mithras.foundation.datacompare.AbstractDataCompare;
import cn.zswltech.mithras.foundation.datacompare.EditdataCompareFactory;
import cn.zswltech.mithras.foundation.datacompare.compare.DefaultDataCompare;
import cn.zswltech.mithras.fund.application.lib.financing.handler.impl.FundFinancingPlanLibHandler;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @create: 2022-08-27
 **/
@Service("fundFinancingPlan")
public class FundFinancingPlanFactory implements EditdataCompareFactory {
    @Resource
    private FundFinancingPlanLibMapper libMapper;
    @Resource
    private FundFinancingPlanLibHandler handler;
    @Resource
    private CommonVersionMapper commonVersionMapper;

    @Override
    public AbstractDataCompare createCompare(List rsps, String version) {
        return new DefaultDataCompare<FundFinancingPlan, FundFinancingPlanLib, FundFinancingPlanDetailRSP>(rsps, libMapper, handler,
                commonVersionMapper, "FUND_FINANCING", version);
    }
}