package cn.zswltech.mithras.credit.application.groupcredit.establish;

import lombok.Data;

import java.util.List;

@Data
public class GroupCreditEstablishProcessStartCommand {

    private boolean createFlow;

    private String approvalType;

    private List<String> bizDeptLeaderIds;

    private List<String> bizDivisionLeaderIds;

    private List<String> riskControlManagerIds;

    private String startUserId;

    private String businessKey;

    private String subModule;

    private String processInstanceName;

    private List<String> ccUserIdList;

    private String startUserDeptId;

    private Long clientId;
}
