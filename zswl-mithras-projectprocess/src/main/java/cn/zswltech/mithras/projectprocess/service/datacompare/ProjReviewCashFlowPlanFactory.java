package cn.zswltech.mithras.projectprocess.service.datacompare;

import cn.zswltech.mithras.dto.projreview.cashflowplan.ProjReviewCashFlowPlanListRSP;
import cn.zswltech.mithras.service.mapper.lib.CommonVersionMapper;
import cn.zswltech.mithras.projectprocess.mapper.lib.projreview.ProjReviewCashFlowPlanLibMapper;
import cn.zswltech.mithras.projectprocess.mapper.model.projreview.ProjReviewCashFlowPlan;
import cn.zswltech.mithras.projectprocess.mapper.model.projreview.ProjReviewCashFlowPlanLib;
import cn.zswltech.mithras.service.service.datacompare.AbstractDataCompare;
import cn.zswltech.mithras.service.service.datacompare.EditdataCompareFactory;
import cn.zswltech.mithras.service.service.datacompare.compare.DefaultDataCompare;
import cn.zswltech.mithras.projectprocess.service.lib.projreview.handler.impl.ProjReviewCashFlowPlanLibHandler;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @create: 2022-08-18
 **/
@Service("projReviewCashFlowPlan")
public class ProjReviewCashFlowPlanFactory implements EditdataCompareFactory {
    @Resource
    private ProjReviewCashFlowPlanLibMapper libMapper;
    @Resource
    private ProjReviewCashFlowPlanLibHandler handler;
    @Resource
    private CommonVersionMapper commonVersionMapper;

    @Override
    public AbstractDataCompare createCompare(List rsps, String version) {
        return new DefaultDataCompare<ProjReviewCashFlowPlan, ProjReviewCashFlowPlanLib, ProjReviewCashFlowPlanListRSP>(rsps,libMapper,handler, commonVersionMapper, "PROJ_REVIEW", version);
    }
}
