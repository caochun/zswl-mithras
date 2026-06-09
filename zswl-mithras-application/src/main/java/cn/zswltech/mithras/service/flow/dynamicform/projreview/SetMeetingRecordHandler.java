package cn.zswltech.mithras.service.flow.dynamicform.projreview;

import cn.hutool.core.lang.Assert;
import cn.zswltech.flow.core.domain.resp.TaskResp;
import cn.zswltech.flow.core.model.ext.UserTaskExt;
import cn.zswltech.mithras.dto.flow.search.TaskDetailRSP;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.workflow.domain.enums.FlowDynamicFormEnum;
import cn.zswltech.mithras.workflow.application.flow.enums.ProcessModelTypeEnum;
import cn.zswltech.mithras.credit.domain.groupcredit.review.enums.GroupCreditReviewMaterialsEnum;
import cn.zswltech.mithras.projectprocess.enums.projreview.ProjReviewMaterialsEnum;
import cn.zswltech.mithras.service.flow.dynamicform.DynamicFormHandler;
import cn.zswltech.mithras.service.mapper.model.MaterialsList;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.service.materialsfile.MaterialsListService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;
import java.util.Optional;

/**
 * @author dingqi
 * @date 2022/10/27
 * @description
 */
@Component
public class SetMeetingRecordHandler implements DynamicFormHandler {
    @Resource
    private MaterialsListService materialsListService;

    @Override
    public void check(Map<String, Object> formMap, TaskResp taskResp, UserTaskExt userTaskExt) {
        BusinessModuleEnum businessModuleEnum = Optional.ofNullable(ProcessModelTypeEnum.getByName(taskResp.getModelKey()))
                .map(ProcessModelTypeEnum::getBusinessModuleName)
                .map(BusinessModuleEnum::of)
                .orElseThrow(() -> new MithrasException("modelKey未登记，请联系管理员进行处理"));
        if (BusinessModuleEnum.PROJ_REVIEW.equals(businessModuleEnum)) {
            LambdaQueryWrapper<MaterialsList> query = Wrappers.lambdaQuery();
            query.eq(MaterialsList::getBusinessType, BusinessModuleEnum.PROJ_REVIEW.name());
            query.eq(MaterialsList::getBelongId, Long.valueOf(taskResp.getBusinessKey()));
            query.eq(MaterialsList::getMaterialsType, ProjReviewMaterialsEnum.MEETING_REVIEW_RECORD.name());
            int count = materialsListService.count(query);
            Assert.isTrue(count > 0, () -> MithrasException.newException("请先上传评审会会议记录"));
        } else if (BusinessModuleEnum.GROUP_CREDIT_REVIEW.equals(businessModuleEnum)) {
            LambdaQueryWrapper<MaterialsList> query = Wrappers.lambdaQuery();
            query.eq(MaterialsList::getBusinessType, BusinessModuleEnum.GROUP_CREDIT_REVIEW.name());
            query.eq(MaterialsList::getBelongId, Long.valueOf(taskResp.getBusinessKey()));
            query.eq(MaterialsList::getMaterialsType, GroupCreditReviewMaterialsEnum.MEETING_REVIEW_RECORD.name());
            int count = materialsListService.count(query);
            Assert.isTrue(count > 0, () -> MithrasException.newException("请先上传评审会会议记录"));
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
        return FlowDynamicFormEnum.projReview_setMeetingRecord;
    }
}
