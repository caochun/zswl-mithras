package cn.zswltech.mithras.application.orchestration.workflow.flow.dynamicform.riskcontrol.opinion;

import cn.zswltech.flow.core.domain.resp.TaskResp;
import cn.zswltech.flow.core.model.ext.UserTaskExt;
import cn.zswltech.mithras.dto.flow.search.TaskDetailRSP;
import cn.zswltech.mithras.riskcontrol.common.RiskControlFlowVariable;
import cn.zswltech.mithras.workflow.enums.FlowDynamicFormEnum;
import cn.zswltech.mithras.workflow.flow.dynamicform.DynamicFormHandler;
import org.flowable.engine.RuntimeService;
import org.springframework.stereotype.Component;

import java.util.Map;

import static cn.hutool.core.util.ObjectUtil.isNull;
import static cn.zswltech.mithras.foundation.exception.MithrasException.err;
import static cn.zswltech.mithras.foundation.context.SpringContextHolder.getBean;

/**
 *
 */
@Component
public class RiskOpinionHandleCheckHandler implements DynamicFormHandler {

    @Override
    public void check(Map<String, Object> formMap, TaskResp taskResp, UserTaskExt userTaskExt) {
        Object o = formMap.get(RiskControlFlowVariable.HANDLE_TYPE);
        if (isNull(o)) {
            err("处理方式不能为空");
        }
    }

    @Override
    public void handle(Map<String, Object> formMap, TaskResp taskResp, UserTaskExt userTaskExt) {
        Integer warnLevel = (Integer) formMap.get(RiskControlFlowVariable.HANDLE_TYPE);
        getBean(RuntimeService.class).setVariable(taskResp.getProcessInstanceId(), RiskControlFlowVariable.HANDLE_TYPE, warnLevel);
    }

    @Override
    public void collect(TaskDetailRSP rsp) {
        /*String businessKey = rsp.getBusinessKey();
        RiskControlOpinionMonitor opinion = getBean(RiskControlOpinionMonitorService.class).getById(Long.valueOf(businessKey));
        rsp.getDynamicFormData().put(HANDLE_TYPE, opinion.getWarnLevel());*/
    }

    @Override
    public FlowDynamicFormEnum getType() {
        return FlowDynamicFormEnum.risk_opinion_handle_type;
    }

}
