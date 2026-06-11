package cn.zswltech.mithras.projectprocess.datacompare;

import cn.zswltech.mithras.dto.projpricing.cashflowplan.ProjPricingCashFlowPlanListRSP;
import cn.zswltech.mithras.foundation.persistence.mapper.CommonVersionMapper;
import cn.zswltech.mithras.projectprocess.mapper.lib.projpricing.ProjPricingCashFlowPlanLibMapper;
import cn.zswltech.mithras.projectprocess.model.projpricing.ProjPricingCashFlowPlan;
import cn.zswltech.mithras.projectprocess.model.projpricing.ProjPricingCashFlowPlanLib;
import cn.zswltech.mithras.foundation.datacompare.AbstractDataCompare;
import cn.zswltech.mithras.foundation.datacompare.EditdataCompareFactory;
import cn.zswltech.mithras.foundation.datacompare.compare.DefaultDataCompare;
import cn.zswltech.mithras.projectprocess.application.lib.projpricing.handler.impl.ProjPricingCashFlowPlanLibHandler;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @create: 2022-08-18
 **/
@Service("projPricingCashFlowPlan")
public class ProjPricingCashFlowPlanFactory implements EditdataCompareFactory {
    @Resource
    private ProjPricingCashFlowPlanLibMapper libMapper;
    @Resource
    private ProjPricingCashFlowPlanLibHandler handler;
    @Resource
    private CommonVersionMapper commonVersionMapper;

    @Override
    public AbstractDataCompare createCompare(List rsps, String version) {
        return new DefaultDataCompare<ProjPricingCashFlowPlan, ProjPricingCashFlowPlanLib, ProjPricingCashFlowPlanListRSP>(rsps,libMapper,handler, commonVersionMapper, "PROJ_PRICING", version);
    }
}
