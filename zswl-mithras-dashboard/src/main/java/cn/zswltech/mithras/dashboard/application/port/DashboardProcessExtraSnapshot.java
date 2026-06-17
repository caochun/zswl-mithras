package cn.zswltech.mithras.dashboard.application.port;

import lombok.Data;

@Data
public class DashboardProcessExtraSnapshot {

    private String processInstanceId;

    private Long clientId;

    private String projName;

    private String projCode;

    private String contractCode;
}
