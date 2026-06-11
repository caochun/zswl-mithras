package cn.zswltech.mithras.application.orchestration.workflow.flow.file.focusfileselector;

import cn.zswltech.mithras.workflow.flow.enums.ProcessModelTypeEnum;
import org.springframework.stereotype.Component;

/**
 * @author dingqi
 * @date 2024/2/5
 * @description 项目评审创建流程-关注文件筛选
 */
@Component
public class ProjReviewCreateFlowFocusFileSelector extends AbstractProjReviewFlowFocusFileSelector {
    @Override
    public ProcessModelTypeEnum processType() {
        return ProcessModelTypeEnum.ProjReviewCreateFlow;
    }
}
