package cn.zswltech.mithras.application.orchestration.workflow.flow.dynamicform.projreview;

import cn.zswltech.flow.core.domain.resp.TaskResp;
import cn.zswltech.flow.core.model.ext.UserTaskExt;
import cn.zswltech.mithras.dto.flow.search.TaskDetailRSP;
import cn.zswltech.mithras.application.orchestration.auth.BusinessModuleEnum;
import cn.zswltech.mithras.workflow.enums.FlowDynamicFormEnum;
import cn.zswltech.mithras.workflow.flow.enums.ProcessModelTypeEnum;
import cn.zswltech.mithras.projectprocess.enums.projreview.ProjReviewMaterialsEnum;
import cn.zswltech.mithras.workflow.flow.dynamicform.DynamicFormHandler;
import cn.zswltech.mithras.document.materialsfile.MaterialsListQueryService;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;
import java.util.Optional;

/**
 * 表单处理器
 *
 */
@Component
public class SetFullCommitteeFileHandler implements DynamicFormHandler {

    @Resource
    private MaterialsListQueryService materialsListQueryService;

    @Override
    public void check(Map<String, Object> formMap, TaskResp taskResp, UserTaskExt userTaskExt) {
        ProcessModelTypeEnum processModelTypeEnum = Optional.ofNullable(ProcessModelTypeEnum.getByName(taskResp.getModelKey()))
                .orElseThrow(() -> new MithrasException("modelKey未登记，请联系管理员进行处理"));
        if (ProcessModelTypeEnum.ProjReviewCreateFlow.equals(processModelTypeEnum)) {
            if (!materialsListQueryService.exists(BusinessModuleEnum.PROJ_REVIEW.name(), ProjReviewMaterialsEnum.PROFESSIONAL_REVIEW_COMMENTS.name(), Long.valueOf(taskResp.getBusinessKey()))) {
                throw new MithrasException("请上传" + ProjReviewMaterialsEnum.PROFESSIONAL_REVIEW_COMMENTS.getDisplay());
            }
        }
        if (ProcessModelTypeEnum.GroupCreditReviewCreateFlow.equals(processModelTypeEnum)) {
            if (!materialsListQueryService.exists(BusinessModuleEnum.GROUP_CREDIT_REVIEW.name(), ProjReviewMaterialsEnum.PROFESSIONAL_REVIEW_COMMENTS.name(), Long.valueOf(taskResp.getBusinessKey()))) {
                throw new MithrasException("请上传" + ProjReviewMaterialsEnum.PROFESSIONAL_REVIEW_COMMENTS.getDisplay());
            }
        }
    }

    @Override
    public void handle(Map<String, Object> formMap, TaskResp taskResp, UserTaskExt userTaskExt) {

    }

    @Override
    public void collect(TaskDetailRSP rsp) {

    }

    @Override
    public FlowDynamicFormEnum getType() {
        return FlowDynamicFormEnum.projReview_setFullCommitteeFile;
    }

}
