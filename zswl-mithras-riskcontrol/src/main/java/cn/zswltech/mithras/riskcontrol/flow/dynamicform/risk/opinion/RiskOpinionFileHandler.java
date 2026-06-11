package cn.zswltech.mithras.riskcontrol.flow.dynamicform.risk.opinion;

import cn.zswltech.flow.core.domain.resp.TaskResp;
import cn.zswltech.flow.core.model.ext.UserTaskExt;
import cn.zswltech.mithras.dto.flow.search.TaskDetailRSP;
import cn.zswltech.mithras.workflow.flow.enums.ProcessModelTypeEnum;
import cn.zswltech.mithras.workflow.enums.FlowDynamicFormEnum;
import cn.zswltech.mithras.workflow.flow.dynamicform.DynamicFormHandler;
import cn.zswltech.mithras.riskcontrol.opinion.RiskControlOpinionMonitor;
import cn.zswltech.mithras.riskcontrol.opinion.RiskControlOpinionMonitorMapper;
import cn.zswltech.mithras.riskcontrol.warning.RiskControlWarnMonitor;
import cn.zswltech.mithras.riskcontrol.warning.RiskControlWarnMonitorMapper;
import org.flowable.engine.RuntimeService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;
import java.util.Optional;

import static cn.hutool.core.util.ObjectUtil.isNull;
import static cn.zswltech.mithras.foundation.exception.MithrasException.err;

/**
 *
 */
@Component
public class RiskOpinionFileHandler implements DynamicFormHandler {
    public static final String ADVISE = "warnLevel";
    private static final String RISK_OPINION_BUSINESS_TYPE = "RISK_OPINION";
    private static final String RISK_MONITOR_BUSINESS_TYPE = "RISK_MONITOR";

    @Resource
    private RuntimeService runtimeService;
    @Resource
    private RiskControlOpinionMonitorMapper riskControlOpinionMonitorMapper;
    @Resource
    private RiskControlWarnMonitorMapper riskControlWarnMonitorMapper;

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
            runtimeService.setVariable(taskResp.getProcessInstanceId(), ADVISE, warnLevel);
        }
    }

    @Override
    public void collect(TaskDetailRSP rsp) {
        String businessKey = rsp.getBusinessKey();
        String businessType = Optional.ofNullable(ProcessModelTypeEnum.getByName(rsp.getModelKey()))
                .map(ProcessModelTypeEnum::getBusinessModuleName)
                .orElse(null);
        if(RISK_OPINION_BUSINESS_TYPE.equals(businessType)) {
            RiskControlOpinionMonitor opinion = riskControlOpinionMonitorMapper.selectById(Long.valueOf(businessKey));
            rsp.getDynamicFormData().put(ADVISE, opinion.getWarnLevel());
        }
        if(RISK_MONITOR_BUSINESS_TYPE.equals(businessType)) {
            RiskControlWarnMonitor opinion = riskControlWarnMonitorMapper.selectById(Long.valueOf(businessKey));
            rsp.getDynamicFormData().put(ADVISE, opinion.getWarnLevel());
        }
    }

    @Override
    public FlowDynamicFormEnum getType() {
        return FlowDynamicFormEnum.risk_opinion_asset_management;
    }

}
