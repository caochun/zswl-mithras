package cn.zswltech.mithras.ftp.newftp.application.port.model;

import lombok.Data;

@Data
public class NewFtpProcessInfo {

    private String processInstanceId;
    private String businessKey;
    private String modelKey;
    private Integer processStatus;
    private String startUserId;
    private String startUserDeptId;
    private boolean startUserNode;
}
