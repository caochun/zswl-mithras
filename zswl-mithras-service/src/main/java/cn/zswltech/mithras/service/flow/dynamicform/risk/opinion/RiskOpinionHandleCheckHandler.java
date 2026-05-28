package cn.zswltech.mithras.service.flow.dynamicform.risk.opinion;

import cn.zswltech.flow.core.domain.resp.TaskResp;
import cn.zswltech.flow.core.model.ext.UserTaskExt;
import cn.zswltech.mithras.dto.flow.search.TaskDetailRSP;
import cn.zswltech.mithras.service.enums.FlowDynamicFormEnum;
import cn.zswltech.mithras.service.flow.dynamicform.DynamicFormHandler;
import org.flowable.engine.RuntimeService;
import org.springframework.stereotype.Component;

import java.util.Map;

import static cn.hutool.core.util.ObjectUtil.isNull;
import static cn.zswltech.mithras.service.others.MithrasException.err;
import static cn.zswltech.mithras.service.others.SpringContextHolder.getBean;

/**
 *
 */
@Component
public class RiskOpinionHandleCheckHandler implements DynamicFormHandler {
    public static final String HANDLE_TYPE = "handleResult";

    @Override
    public void check(Map<String, Object> formMap, TaskResp taskResp, UserTaskExt userTaskExt) {
        Object o = formMap.get(HANDLE_TYPE);
        if (isNull(o)) {
            err("处理方式不能为空");
        }
    }

    @Override
    public void handle(Map<String, Object> formMap, TaskResp taskResp, UserTaskExt userTaskExt) {
        Integer warnLevel = (Integer) formMap.get(HANDLE_TYPE);
        getBean(RuntimeService.class).setVariable(taskResp.getProcessInstanceId(), HANDLE_TYPE, warnLevel);
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
