package cn.zswltech.mithras.application.orchestration.workflow.flow.file.focusfileselector;

import cn.zswltech.mithras.application.orchestration.auth.BusinessModuleEnum;
import cn.zswltech.mithras.projectprocess.enums.projreview.ProjReviewMaterialsEnum;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * @author dingqi
 * @date 2024/2/5
 * @description
 */
@Component
public abstract class AbstractProjReviewFlowFocusFileSelector extends AbstractFlowFocusFileSelector {
    @Override
    protected List<String> bizImportantFileTypes() {
        return Arrays.asList(
                ProjReviewMaterialsEnum.RISK_REVIEW_REPORT.name(),
                ProjReviewMaterialsEnum.LEGAL_COMPLIANCE_REPORT.name(),
                ProjReviewMaterialsEnum.YIELD_REVIEW_REPORT.name()
        );
    }

    @Override
    protected List<String> meetingDecisionFileTypes() {
        return Arrays.asList(
                ProjReviewMaterialsEnum.MEETING_REVIEW_REPORT.name(),
                ProjReviewMaterialsEnum.DIRECTOR_MEETING_REPORT.name()
        );
    }

    @Override
    protected BusinessModuleEnum businessModule() {
        return BusinessModuleEnum.PROJ_REVIEW;
    }
}
