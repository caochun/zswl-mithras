package cn.zswltech.mithras.application.orchestration.workflow.flow.dynamicform.projreview;

import cn.zswltech.flow.core.domain.resp.TaskResp;
import cn.zswltech.flow.core.model.ext.UserTaskExt;
import cn.zswltech.mithras.dto.flow.search.TaskDetailRSP;
import cn.zswltech.mithras.application.orchestration.enums.BusinessModuleEnum;
import cn.zswltech.mithras.workflow.enums.FlowDynamicFormEnum;
import cn.zswltech.mithras.workflow.flow.enums.ProcessModelTypeEnum;
import cn.zswltech.mithras.projectprocess.enums.projreview.ProjReviewMaterialsEnum;
import cn.zswltech.mithras.workflow.flow.dynamicform.DynamicFormHandler;
import cn.zswltech.mithras.document.mapper.MaterialsListMapper;
import cn.zswltech.mithras.document.mapper.model.MaterialsList;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
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
    private MaterialsListMapper materialsListMapper;

    @Override
    public void check(Map<String, Object> formMap, TaskResp taskResp, UserTaskExt userTaskExt) {
        ProcessModelTypeEnum processModelTypeEnum = Optional.ofNullable(ProcessModelTypeEnum.getByName(taskResp.getModelKey()))
                .orElseThrow(() -> new MithrasException("modelKey未登记，请联系管理员进行处理"));
        if (ProcessModelTypeEnum.ProjReviewCreateFlow.equals(processModelTypeEnum)) {
            Integer fileCount = materialsListMapper.selectCount(Wrappers.<MaterialsList>lambdaQuery()
                    .eq(MaterialsList::getBelongId, Long.valueOf(taskResp.getBusinessKey()))
                    .eq(MaterialsList::getMaterialsType, ProjReviewMaterialsEnum.PROFESSIONAL_REVIEW_COMMENTS.name())
                    .eq(MaterialsList::getBusinessType, BusinessModuleEnum.PROJ_REVIEW.name())
            );
            if (fileCount == 0) {
                throw new MithrasException("请上传" + ProjReviewMaterialsEnum.PROFESSIONAL_REVIEW_COMMENTS.getDisplay());
            }
        }
        if (ProcessModelTypeEnum.GroupCreditReviewCreateFlow.equals(processModelTypeEnum)) {
            Integer fileCount = materialsListMapper.selectCount(Wrappers.<MaterialsList>lambdaQuery()
                    .eq(MaterialsList::getBelongId, Long.valueOf(taskResp.getBusinessKey()))
                    .eq(MaterialsList::getMaterialsType, ProjReviewMaterialsEnum.PROFESSIONAL_REVIEW_COMMENTS.name())
                    .eq(MaterialsList::getBusinessType, BusinessModuleEnum.GROUP_CREDIT_REVIEW.name())
            );
            if (fileCount == 0) {
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
