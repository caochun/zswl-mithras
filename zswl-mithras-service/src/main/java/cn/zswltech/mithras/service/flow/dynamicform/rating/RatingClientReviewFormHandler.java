package cn.zswltech.mithras.service.flow.dynamicform.rating;

import cn.zswltech.flow.core.domain.resp.TaskResp;
import cn.zswltech.flow.core.model.ext.UserTaskExt;
import cn.zswltech.mithras.dto.flow.search.TaskDetailRSP;
import cn.zswltech.mithras.workflow.domain.enums.FlowDynamicFormEnum;
import cn.zswltech.mithras.service.flow.dynamicform.DynamicFormHandler;
import cn.zswltech.mithras.service.mapper.MaterialsListMapper;

import javax.annotation.Resource;
import java.util.Map;

public class RatingClientReviewFormHandler implements DynamicFormHandler {


    @Override
    public void check(Map<String, Object> formMap, TaskResp taskResp, UserTaskExt userTaskExt) {

    }

    @Override
    public void handle(Map<String, Object> formMap, TaskResp taskResp, UserTaskExt userTaskExt) {

    }

    @Override
    public void collect(TaskDetailRSP rsp) {

    }

    @Override
    public FlowDynamicFormEnum getType() {
        return FlowDynamicFormEnum.ratingClient_reviewSetFile;
    }

}
