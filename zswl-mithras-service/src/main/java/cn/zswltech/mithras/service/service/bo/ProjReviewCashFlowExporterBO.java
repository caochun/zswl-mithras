package cn.zswltech.mithras.service.service.bo;

import cn.zswltech.mithras.service.mapper.model.projreview.ProjReviewBaseInfo;
import cn.zswltech.mithras.service.mapper.model.projreview.ProjReviewCashFlowPlan;
import cn.zswltech.mithras.service.mapper.model.projreview.ProjReviewFactoringPrice;
import lombok.Data;

import java.util.List;

/**
 * @author dingqi
 * @date 2022/12/20
 * @description
 */
@Data
public class ProjReviewCashFlowExporterBO {
    private ProjReviewBaseInfo projReviewBaseInfo;
    private List<ProjReviewCashFlowPlan> projReviewCashFlowPlanList;
    private String version;
}
