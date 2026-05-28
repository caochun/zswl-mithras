package cn.zswltech.mithras.service.flow.dynamicform.risk.earlywarning;

import cn.zswltech.mithras.service.constant.FlowConstants;
import cn.zswltech.mithras.service.enums.FlowDynamicFormEnum;
import cn.zswltech.mithras.service.flow.dynamicform.risk.AbstractAssetManagementHandler;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

/**
 * 表单处理器（预警统一监测（已放款）-资产管理复核表单）
 *
 * @author gxy
 * @date 2026/1/30
 */
@Component
public class EarlyWarningMonitorAfterLoanHandler extends AbstractAssetManagementHandler implements InitializingBean {

    @Override
    protected void init() {
        assetManagementAndReviewNodeIdMap.put(FlowConstants.EARLY_WARNING_MONITOR_AFTER_LOAN_RED_LIGHT, "assetManagementReview");
        assetManagementAndReviewNodeIdMap.put(FlowConstants.EARLY_WARNING_MONITOR_AFTER_LOAN_OTHER_LIGHT, "Activity_19o6c5h");
    }

    @Override
    public FlowDynamicFormEnum getType() {
        return FlowDynamicFormEnum.early_warning_monitor_after_loan_assetManager;
    }
}
