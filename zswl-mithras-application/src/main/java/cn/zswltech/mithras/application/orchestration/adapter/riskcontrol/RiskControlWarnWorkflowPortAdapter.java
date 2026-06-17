package cn.zswltech.mithras.application.orchestration.adapter.riskcontrol;

import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.flow.core.api.FlowTaskApiService;
import cn.zswltech.flow.core.domain.req.task.ProcessPageReq;
import cn.zswltech.flow.core.domain.resp.ProcessResp;
import cn.zswltech.mithras.riskcontrol.application.port.RiskControlWarnWorkflowInstance;
import cn.zswltech.mithras.riskcontrol.application.port.RiskControlWarnWorkflowPort;
import cn.zswltech.mithras.riskcontrol.common.RiskControlFlowVariable;
import org.flowable.engine.RuntimeService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class RiskControlWarnWorkflowPortAdapter implements RiskControlWarnWorkflowPort {

    @Resource
    private FlowTaskApiService flowTaskApiService;
    @Resource
    private RuntimeService runtimeService;

    @Override
    public List<RiskControlWarnWorkflowInstance> queryWarnProcesses(Collection<String> modelKeys, LocalDate createTimeFrom) {
        ProcessPageReq req = new ProcessPageReq();
        req.setModelKeyList(modelKeys == null ? null : modelKeys.stream().collect(Collectors.toList()));
        req.setSortType(1);
        req.setPageIndex(1);
        req.setPageSize(5000);
        if (createTimeFrom != null) {
            req.setProcessCreateTimeFrom(Date.from(createTimeFrom.atStartOfDay(ZoneId.systemDefault()).toInstant()));
        }
        cn.zswltech.flow.core.util.Page<ProcessResp> page = flowTaskApiService.queryProcess(req);
        if (ObjectUtil.isEmpty(page) || ObjectUtil.isEmpty(page.getContents())) {
            return Collections.emptyList();
        }
        return page.getContents().stream()
                .map(process -> new RiskControlWarnWorkflowInstance(process.getBusinessKey(), process.getStartTime(), process.getEndTime()))
                .collect(Collectors.toList());
    }

    @Override
    public void setHandleResult(String processInstanceId, Object handleResult) {
        runtimeService.setVariable(processInstanceId, RiskControlFlowVariable.HANDLE_TYPE, handleResult);
    }
}
