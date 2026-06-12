package cn.zswltech.mithras.application.orchestration.workflow.flow.dynamicform.projectprocess.projreview;

import cn.zswltech.flow.core.domain.resp.TaskResp;
import cn.zswltech.flow.core.model.ext.UserTaskExt;
import cn.zswltech.mithras.dto.flow.search.TaskDetailRSP;
import cn.zswltech.mithras.workflow.enums.FlowDynamicFormEnum;
import cn.zswltech.mithras.workflow.flow.dynamicform.DynamicFormHandler;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author dingqi
 * @date 2024/3/10
 * @description 项目评审-风控节点校验财报，内部无业务逻辑，纯粹让前端知道当前流程节点需要在通过流程前调用财报校验接口
 */
@Component
public class CheckCorpSubjectItemHandler implements DynamicFormHandler {
    @Override
    public void handle(Map<String, Object> formMap, TaskResp taskResp, UserTaskExt userTaskExt) {

    }

    @Override
    public void collect(TaskDetailRSP rsp) {
        
    }

    @Override
    public FlowDynamicFormEnum getType() {
        return FlowDynamicFormEnum.projReview_checkCorpSubjectItem;
    }
}
