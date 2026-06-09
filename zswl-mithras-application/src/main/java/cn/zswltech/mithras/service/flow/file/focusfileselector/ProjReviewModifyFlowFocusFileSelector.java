package cn.zswltech.mithras.service.flow.file.focusfileselector;

import cn.zswltech.mithras.workflow.application.flow.enums.ProcessModelTypeEnum;
import org.springframework.stereotype.Component;

/**
 * @author dingqi
 * @date 2024/2/5
 * @description 项目评审变更流程-关注文件筛选
 */
@Component
public class ProjReviewModifyFlowFocusFileSelector extends AbstractProjReviewFlowFocusFileSelector {
    @Override
    public ProcessModelTypeEnum processType() {
        return ProcessModelTypeEnum.ProjReviewModifyFlow;
    }
}
