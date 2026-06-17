package cn.zswltech.mithras.leaseholdproperty.application;

import java.util.List;
import java.util.Map;

public interface AppraisalCompanyWhitelistWorkflowPort {

    String start(String modelKey,
                 String processInstanceName,
                 String businessKey,
                 String startUserId,
                 String startUserDeptId,
                 Map<String, ?> variables);

    boolean hasRunningProcess(List<String> modelKeys, String businessKey);
}
