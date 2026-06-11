package cn.zswltech.mithras.projectprocess.application.bo;

import cn.zswltech.mithras.projectprocess.mapper.model.projreview.ProjReviewBaseInfo;
import cn.zswltech.mithras.projectprocess.mapper.model.projreview.ProjReviewCashFlowPlan;
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
