package cn.zswltech.mithras.application.orchestration.adapter.leaseholdproperty;

import cn.zswltech.flow.core.api.FlowProcessApiService;
import cn.zswltech.flow.core.api.FlowTaskApiService;
import cn.zswltech.flow.core.domain.req.StartProcessReq;
import cn.zswltech.flow.core.domain.req.task.ProcessPageReq;
import cn.zswltech.flow.core.enums.ProcessBusinessStatusEnum;
import cn.zswltech.mithras.leaseholdproperty.application.AppraisalCompanyWhitelistWorkflowPort;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Component
public class AppraisalCompanyWhitelistWorkflowPortAdapter implements AppraisalCompanyWhitelistWorkflowPort {

    @Resource
    private FlowProcessApiService flowProcessApiService;
    @Resource
    private FlowTaskApiService flowTaskApiService;

    @Override
    public String start(String modelKey,
                        String processInstanceName,
                        String businessKey,
                        String startUserId,
                        String startUserDeptId,
                        Map<String, ?> variables) {
        StartProcessReq startProcessReq = new StartProcessReq();
        startProcessReq.setModelKey(modelKey);
        startProcessReq.setProcessInstanceName(processInstanceName);
        startProcessReq.setBusinessKey(businessKey);
        startProcessReq.setStartUserId(startUserId);
        startProcessReq.setStartUserDeptId(startUserDeptId);
        startProcessReq.setVariables(copyVariables(variables));
        return flowProcessApiService.start(startProcessReq);
    }

    @Override
    public boolean hasRunningProcess(List<String> modelKeys, String businessKey) {
        ProcessPageReq processPageReq = new ProcessPageReq();
        processPageReq.setModelKeyList(modelKeys);
        processPageReq.setBusinessKey(businessKey);
        processPageReq.setProcessStatusList(Collections.singletonList(ProcessBusinessStatusEnum.RUNNING.getType()));
        Long flowCount = flowTaskApiService.queryProcessCount(processPageReq);
        return Objects.nonNull(flowCount) && flowCount > 0;
    }

    private Map<String, Object> copyVariables(Map<String, ?> variables) {
        if (variables == null) {
            return Collections.emptyMap();
        }
        return new HashMap<>(variables);
    }
}
