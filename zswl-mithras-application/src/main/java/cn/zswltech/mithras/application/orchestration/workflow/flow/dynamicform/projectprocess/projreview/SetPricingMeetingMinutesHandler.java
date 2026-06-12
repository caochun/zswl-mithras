package cn.zswltech.mithras.application.orchestration.workflow.flow.dynamicform.projectprocess.projreview;

import cn.zswltech.flow.core.domain.resp.TaskResp;
import cn.zswltech.flow.core.model.ext.UserTaskExt;
import cn.zswltech.mithras.dto.flow.search.TaskDetailRSP;
import cn.zswltech.mithras.workflow.enums.FlowDynamicFormEnum;
import cn.zswltech.mithras.projectprocess.enums.projreview.ProjReviewMaterialsEnum;
import cn.zswltech.mithras.workflow.flow.dynamicform.DynamicFormHandler;
import cn.zswltech.mithras.document.persistence.mapper.MaterialsListMapper;
import cn.zswltech.mithras.document.persistence.model.MaterialsList;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;

/**
 * 总经办会议纪要
 *
 * @author zhaozhengkang
 * @date 2022/8/30 15:44 AM
 */
@Component
public class SetPricingMeetingMinutesHandler implements DynamicFormHandler {
    private static final String PROJ_REVIEW_BUSINESS_TYPE = "PROJ_REVIEW";

    @Resource
    private MaterialsListMapper materialsListMapper;

    @Override
    public void check(Map<String, Object> formMap, TaskResp taskResp, UserTaskExt userTaskExt) {
        Integer fileCount = materialsListMapper.selectCount(Wrappers.<MaterialsList>lambdaQuery()
                .eq(MaterialsList::getBelongId, Long.valueOf(taskResp.getBusinessKey()))
                .eq(MaterialsList::getMaterialsType, ProjReviewMaterialsEnum.GMO_MEETING_MINUTES.name())
                .eq(MaterialsList::getBusinessType, PROJ_REVIEW_BUSINESS_TYPE)
        );
        if (fileCount == 0) {
            throw new MithrasException("请上传" + ProjReviewMaterialsEnum.GMO_MEETING_MINUTES.getDisplay());
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
        return FlowDynamicFormEnum.projReview_setPricingMeetingMinutes;
    }

}
