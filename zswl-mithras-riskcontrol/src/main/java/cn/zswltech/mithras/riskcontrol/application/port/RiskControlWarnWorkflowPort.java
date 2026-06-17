package cn.zswltech.mithras.riskcontrol.application.port;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

public interface RiskControlWarnWorkflowPort {

    List<RiskControlWarnWorkflowInstance> queryWarnProcesses(Collection<String> modelKeys, LocalDate createTimeFrom);

    void setHandleResult(String processInstanceId, Object handleResult);
}
