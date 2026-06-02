package cn.zswltech.mithras.service.flow.dynamicform.risk.opinion;

import cn.zswltech.flow.core.domain.resp.TaskResp;
import cn.zswltech.flow.core.model.ext.UserTaskExt;
import cn.zswltech.mithras.dto.flow.search.TaskDetailRSP;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.enums.FlowDynamicFormEnum;
import cn.zswltech.mithras.service.flow.dynamicform.DynamicFormHandler;
import cn.zswltech.mithras.riskcontrol.opinion.RiskControlOpinionMonitor;
import cn.zswltech.mithras.riskcontrol.warning.RiskControlWarnMonitor;
import cn.zswltech.mithras.service.service.riskcontrol.RiskControlOpinionMonitorService;
import cn.zswltech.mithras.service.service.riskcontrol.RiskControlWarnMonitorService;
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
public class RiskOpinionFileHandler implements DynamicFormHandler {
    public static final String ADVISE = "warnLevel";

    @Override
    public void check(Map<String, Object> formMap, TaskResp taskResp, UserTaskExt userTaskExt) {
        Object o = formMap.get(ADVISE);
        if (isNull(o)) {
            err("预警信号不能为空");
        }
    }

    @Override
    public void handle(Map<String, Object> formMap, TaskResp taskResp, UserTaskExt userTaskExt) {
        Integer warnLevel = (Integer) formMap.get(ADVISE);
        if (warnLevel > 2) {
            getBean(RuntimeService.class).setVariable(taskResp.getProcessInstanceId(), ADVISE, warnLevel);
        }
    }

    @Override
    public void collect(TaskDetailRSP rsp) {
        String businessKey = rsp.getBusinessKey();
        if(BusinessModuleEnum.RISK_OPINION.getModelKeyList().contains(rsp.getModelKey())) {
            RiskControlOpinionMonitor opinion = getBean(RiskControlOpinionMonitorService.class).getById(Long.valueOf(businessKey));
            rsp.getDynamicFormData().put(ADVISE, opinion.getWarnLevel());
        }
        if(BusinessModuleEnum.RISK_MONITOR.getModelKeyList().contains(rsp.getModelKey())) {
            RiskControlWarnMonitor opinion = getBean(RiskControlWarnMonitorService.class).getById(Long.valueOf(businessKey));
            rsp.getDynamicFormData().put(ADVISE, opinion.getWarnLevel());
        }
    }

    @Override
    public FlowDynamicFormEnum getType() {
        return FlowDynamicFormEnum.risk_opinion_asset_management;
    }

}
