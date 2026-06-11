package cn.zswltech.mithras.projectprocess.application.bo;

import cn.zswltech.mithras.projectprocess.model.projpricing.ProjPricingBaseInfo;
import cn.zswltech.mithras.projectprocess.model.projpricing.ProjPricingCashFlowPlan;
import lombok.Data;

import java.util.List;

/**
 * @author dingqi
 * @date 2022/12/20
 * @description
 */
@Data
public class ProjPricingCashFlowExporterBO {
    private ProjPricingBaseInfo projPricingBaseInfo;
    private List<ProjPricingCashFlowPlan> projPricingCashFlowPlanList;
    private String version;
}
