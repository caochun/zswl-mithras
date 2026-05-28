package cn.zswltech.mithras.service.flow.dynamicform.projreview;

import cn.zswltech.flow.core.domain.resp.TaskResp;
import cn.zswltech.flow.core.model.ext.UserTaskExt;
import cn.zswltech.mithras.dto.flow.search.TaskDetailRSP;
import cn.zswltech.mithras.service.enums.FlowDynamicFormEnum;
import cn.zswltech.mithras.service.flow.dynamicform.DynamicFormHandler;
import cn.zswltech.mithras.service.service.materialsfile.MaterialsListService;
import cn.zswltech.mithras.service.service.projreview.ProjReviewMeetMinuteBaseInfoService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;

/**
 * 表单处理器
 *
 * @author wangchuanhao
 * @date 2022/8/8 11:44 AM
 */
@Component
public class SetMeetingFileHandler implements DynamicFormHandler {
    @Resource
    private MaterialsListService materialsListService;
    @Resource
    private ProjReviewMeetMinuteBaseInfoService projReviewMeetMinuteBaseInfoService;

    @Override
    public void check(Map<String, Object> formMap, TaskResp taskResp, UserTaskExt userTaskExt) {
        /*BusinessModuleEnum businessModuleEnum = Optional.ofNullable(ProcessModelTypeEnum.getByName(taskResp.getModelKey()))
                .map(ProcessModelTypeEnum::getBusinessModuleName)
                .map(BusinessModuleEnum::of)
                .orElseThrow(() -> new MithrasException("modelKey未登记，请联系管理员进行处理"));
        //判断是否存在线上会议纪要
        ProjReviewMeetMinuteBaseInfoDetailREQ meetMinuteBaseInfoDetailREQ = new ProjReviewMeetMinuteBaseInfoDetailREQ();
        meetMinuteBaseInfoDetailREQ.setProjFlowId(taskResp.getProcessInstanceId());
        ProjReviewMeetMinuteBaseInfoDetailRSP reviewMeetMinuteBaseInfoDetailRSP = projReviewMeetMinuteBaseInfoService.detail(meetMinuteBaseInfoDetailREQ);
        if (ObjectUtil.isNotEmpty(reviewMeetMinuteBaseInfoDetailRSP)) {
            return;
        }
        if (BusinessModuleEnum.PROJ_REVIEW.equals(businessModuleEnum)) {
            LambdaQueryWrapper<MaterialsList> query = Wrappers.lambdaQuery();
            query.eq(MaterialsList::getBusinessType, BusinessModuleEnum.PROJ_REVIEW.name());
            query.eq(MaterialsList::getBelongId, Long.valueOf(taskResp.getBusinessKey()));
            query.eq(MaterialsList::getMaterialsType, ProjReviewMaterialsEnum.MEETING_REVIEW_REPORT.name());
            int count = materialsListService.count(query);
            Assert.isTrue(count > 0, () -> MithrasException.newException("请先上传评审会会议纪要"));
        } else if (BusinessModuleEnum.GROUP_CREDIT_REVIEW.equals(businessModuleEnum)) {
            LambdaQueryWrapper<MaterialsList> query = Wrappers.lambdaQuery();
            query.eq(MaterialsList::getBusinessType, BusinessModuleEnum.GROUP_CREDIT_REVIEW.name());
            query.eq(MaterialsList::getBelongId, Long.valueOf(taskResp.getBusinessKey()));
            query.eq(MaterialsList::getMaterialsType, GroupCreditReviewMaterialsEnum.MEETING_REVIEW_REPORT.name());
            int count = materialsListService.count(query);
            Assert.isTrue(count > 0, () -> MithrasException.newException("请先上传评审会会议纪要"));
        }*/
    }

    @Override
    public void handle(Map<String, Object> formMap, TaskResp taskResp, UserTaskExt userTaskExt) {
    }

    @Override
    public void collect(TaskDetailRSP rsp) {

    }

    @Override
    public FlowDynamicFormEnum getType() {
        return FlowDynamicFormEnum.projReview_setMeetingFile;
    }

}
