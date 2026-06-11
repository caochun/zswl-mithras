package cn.zswltech.mithras.payment.flow.dynamicform.payment;

import cn.zswltech.flow.core.domain.resp.TaskResp;
import cn.zswltech.flow.core.model.ext.UserTaskExt;
import cn.zswltech.mithras.dto.flow.search.TaskDetailRSP;
import cn.zswltech.mithras.workflow.enums.FlowDynamicFormEnum;
import cn.zswltech.mithras.workflow.flow.dynamicform.DynamicFormHandler;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author bigbear
 * @date 2024/12/23 11:46
 * @className CurrentNodeIsNeedCheckHandler
 * @description 给前端判断接口调用的标识
 */
@Component
public class CurrentNodeIsNeedCheckHandler implements DynamicFormHandler {

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
        return FlowDynamicFormEnum.payment_checkApproveAmount;
    }
}
