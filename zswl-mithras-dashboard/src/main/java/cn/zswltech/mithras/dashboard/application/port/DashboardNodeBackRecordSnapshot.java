package cn.zswltech.mithras.dashboard.application.port;

import lombok.Data;

@Data
public class DashboardNodeBackRecordSnapshot {

    private String processInstanceId;

    private Integer jumpToSourceFlag;
}
