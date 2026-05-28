package cn.zswltech.mithras.service.flow.dynamicform;

import cn.zswltech.flow.core.domain.resp.TaskResp;
import cn.zswltech.flow.core.model.ext.UserTaskExt;
import cn.zswltech.mithras.dto.flow.search.TaskDetailRSP;
import cn.zswltech.mithras.service.enums.FlowDynamicFormEnum;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author dingqi
 * @date 2024/7/16
 * @description
 */
@Component
public class CommonNoticeAddContractHandler implements DynamicFormHandler {
    @Override
    public void handle(Map<String, Object> formMap, TaskResp taskResp, UserTaskExt userTaskExt) {
        // do nothing
    }

    @Override
    public void collect(TaskDetailRSP rsp) {
        // do nothing
    }

    @Override
    public FlowDynamicFormEnum getType() {
        return FlowDynamicFormEnum.notice_addContractCode;
    }
}
