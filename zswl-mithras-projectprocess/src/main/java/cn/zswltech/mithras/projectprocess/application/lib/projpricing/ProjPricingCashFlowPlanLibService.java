package cn.zswltech.mithras.projectprocess.application.lib.projpricing;

import cn.zswltech.mithras.dto.projpricing.cashflowplan.ProjPricingCashFlowPlanListREQ;
import cn.zswltech.mithras.dto.projpricing.cashflowplan.ProjPricingCashFlowPlanListRSP;
import cn.zswltech.mithras.projectprocess.model.projpricing.ProjPricingCashFlowPlanLib;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 
 * @author: jackerhe 
 * @date: 2022/8/2 10:39 上午
 **/
public interface ProjPricingCashFlowPlanLibService extends IService<ProjPricingCashFlowPlanLib> {
    List<ProjPricingCashFlowPlanListRSP> list(ProjPricingCashFlowPlanListREQ req);

    List<ProjPricingCashFlowPlanLib> listByProjPricingIdAndVersion(Long projReviewId, String version);
}
