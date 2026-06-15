package cn.zswltech.mithras.ftp.oldftp.service.port;

import lombok.Data;

@Data
public class FtpGuidanceProcessInfo {

    private String processInstanceId;
    private String businessKey;
    private String modelKey;
    private String modelDisplayName;
    private Integer processStatus;
    private String startUserId;
    private String startUserDeptId;
    private boolean startUserNode;
}
