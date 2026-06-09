package cn.zswltech.mithras.fund.application.datacompare;

import cn.zswltech.mithras.dto.fund.financing.plan.FundFinancingPlanDetailRSP;
import cn.zswltech.mithras.service.mapper.lib.CommonVersionMapper;
import cn.zswltech.mithras.fund.infrastructure.persistence.mapper.lib.financing.FundFinancingPlanLibMapper;
import cn.zswltech.mithras.fund.infrastructure.persistence.mapper.model.financing.FundFinancingPlan;
import cn.zswltech.mithras.fund.infrastructure.persistence.mapper.model.financing.FundFinancingPlanLib;
import cn.zswltech.mithras.service.service.datacompare.AbstractDataCompare;
import cn.zswltech.mithras.service.service.datacompare.EditdataCompareFactory;
import cn.zswltech.mithras.service.service.datacompare.compare.DefaultDataCompare;
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