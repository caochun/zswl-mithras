package cn.zswltech.mithras.dashboard.application.port;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class DashboardAfterLeasePrepareSnapshot {

    private Long id;

    private String businessId;

    private String formName;

    private String processStatusCode;

    private String processStatusDisplay;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
