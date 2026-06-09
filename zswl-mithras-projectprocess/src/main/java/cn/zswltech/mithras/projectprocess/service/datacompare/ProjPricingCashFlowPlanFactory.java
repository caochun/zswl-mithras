package cn.zswltech.mithras.projectprocess.service.datacompare;

import cn.zswltech.mithras.dto.projpricing.cashflowplan.ProjPricingCashFlowPlanListRSP;
import cn.zswltech.mithras.service.mapper.lib.CommonVersionMapper;
import cn.zswltech.mithras.projectprocess.mapper.lib.projpricing.ProjPricingCashFlowPlanLibMapper;
import cn.zswltech.mithras.projectprocess.mapper.model.projpricing.ProjPricingCashFlowPlan;
import cn.zswltech.mithras.projectprocess.mapper.model.projpricing.ProjPricingCashFlowPlanLib;
import cn.zswltech.mithras.service.service.datacompare.AbstractDataCompare;
import cn.zswltech.mithras.service.service.datacompare.EditdataCompareFactory;
import cn.zswltech.mithras.service.service.datacompare.compare.DefaultDataCompare;
import cn.zswltech.mithras.projectprocess.service.lib.projpricing.handler.impl.ProjPricingCashFlowPlanLibHandler;
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
