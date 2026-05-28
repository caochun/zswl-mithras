package cn.zswltech.mithras.service.flow.file.focusfileselector;

import cn.zswltech.mithras.service.enums.ProcessModelTypeEnum;
import org.springframework.stereotype.Component;

/**
 * @author dingqi
 * @date 2024/2/22
 * @description 项目定价创建流程-关注文件筛选
 */
@Component
public class ProjReviewPricingApprovalFlowFocusFileSelector extends AbstractProjReviewFlowFocusFileSelector {
    @Override
    public ProcessModelTypeEnum processType() {
        return ProcessModelTypeEnum.ProjReviewPricingApprovalFlow;
    }
}
