package cn.zswltech.mithras.application.orchestration.workflow.flow.dynamicform.riskcontrol.opinion;

import cn.zswltech.flow.core.domain.resp.TaskResp;
import cn.zswltech.flow.core.model.ext.UserTaskExt;
import cn.zswltech.mithras.dto.flow.search.TaskDetailRSP;
import cn.zswltech.mithras.workflow.enums.FlowDynamicFormEnum;
import cn.zswltech.mithras.workflow.flow.dynamicform.DynamicFormHandler;
import cn.zswltech.mithras.riskcontrol.opinion.RiskControlOpinionMonitor;
import cn.zswltech.mithras.riskcontrol.opinion.RiskControlOpinionMonitorMapper;
import cn.zswltech.mithras.riskcontrol.warning.RiskControlWarnMonitor;
import cn.zswltech.mithras.riskcontrol.warning.RiskControlWarnMonitorMapper;
import org.flowable.engine.RuntimeService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static cn.hutool.core.util.ObjectUtil.isNull;
import static cn.zswltech.mithras.foundation.exception.MithrasException.err;

/**
 *
 */
@Component
public class RiskOpinionFileHandler implements DynamicFormHandler {
    public static final String ADVISE = "warnLevel";
    private static final Set<String> RISK_OPINION_MODEL_KEYS = new HashSet<>(Arrays.asList(
            "RiskControlOpinionHandleFlow",
            "RiskControlOpinionHandleAfterLaunchFlow",
            "RiskControlOpinionHandleCloseFlow",
            "RiskControlOpinionHandleAfterLaunchCloseFlow",
            "RiskControlNotPaymentFlow",
            "RiskControlPaymentFlow"));
    private static final Set<String> RISK_WARN_MODEL_KEYS = new HashSet<>(Arrays.asList(
            "RiskControlWarnNotPaymentFlow",
            "RiskControlWarnPaymentFlow"));

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
        if (RISK_OPINION_MODEL_KEYS.contains(rsp.getModelKey())) {
            RiskControlOpinionMonitor opinion = riskControlOpinionMonitorMapper.selectById(Long.valueOf(businessKey));
            rsp.getDynamicFormData().put(ADVISE, opinion.getWarnLevel());
        }
        if (RISK_WARN_MODEL_KEYS.contains(rsp.getModelKey())) {
            RiskControlWarnMonitor opinion = riskControlWarnMonitorMapper.selectById(Long.valueOf(businessKey));
            rsp.getDynamicFormData().put(ADVISE, opinion.getWarnLevel());
        }
    }

    @Override
    public FlowDynamicFormEnum getType() {
        return FlowDynamicFormEnum.risk_opinion_asset_management;
    }

}
