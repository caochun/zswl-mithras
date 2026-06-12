package cn.zswltech.mithras.blackgray.port;

public interface BlackGrayApprovalProcessPort {

    String start(BlackGrayApprovalProcessType type, Long businessId, String processInstanceName, String startUserDeptId);
}
