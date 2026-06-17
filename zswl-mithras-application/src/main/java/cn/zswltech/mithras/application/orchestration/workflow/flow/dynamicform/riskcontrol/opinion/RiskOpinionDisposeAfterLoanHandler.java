package cn.zswltech.mithras.application.orchestration.workflow.flow.dynamicform.riskcontrol.opinion;

import cn.zswltech.mithras.workflow.flow.constant.FlowConstants;
import cn.zswltech.mithras.workflow.enums.FlowDynamicFormEnum;
import cn.zswltech.mithras.application.orchestration.workflow.flow.dynamicform.riskcontrol.AbstractAssetManagementHandler;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

/**
 * 表单处理器（舆情处置（已放款）-资产管理复核表单）
 *
 * @author gxy
 * @date 2026/1/30
 */
@Component
public class RiskOpinionDisposeAfterLoanHandler extends AbstractAssetManagementHandler implements InitializingBean {

    @Override
    protected void init() {
        assetManagementAndReviewNodeIdMap.put(FlowConstants.RISK_OPINION_DISPOSE_AFTER_LOAN_RED_LIGHT, "assetManagementReview");
        assetManagementAndReviewNodeIdMap.put(FlowConstants.RISK_OPINION_DISPOSE_AFTER_LOAN_OTHER_LIGHT, "Activity_19o6c5h");
    }

    @Override
    public FlowDynamicFormEnum getType() {
        return FlowDynamicFormEnum.risk_opinion_dispose_after_loan_assetManager;
    }
}
