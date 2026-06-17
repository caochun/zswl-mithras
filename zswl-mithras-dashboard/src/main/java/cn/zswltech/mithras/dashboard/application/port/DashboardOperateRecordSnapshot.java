package cn.zswltech.mithras.dashboard.application.port;

import lombok.Data;

import java.util.Date;

@Data
public class DashboardOperateRecordSnapshot {

    private String processDefinitionId;

    private String processInstanceId;

    private String type;

    private String note;

    private String handlerId;

    private String taskActivityId;

    private Date operateTime;

    public String getModelKey() {
        if (processDefinitionId == null) {
            return null;
        }
        return processDefinitionId.split(":")[0];
    }
}
