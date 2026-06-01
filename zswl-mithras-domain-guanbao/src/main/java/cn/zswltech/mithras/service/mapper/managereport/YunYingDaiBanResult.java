package cn.zswltech.mithras.service.mapper.managereport;

import lombok.Data;

/**
 * @author dingqi
 * @date 2024/12/13
 * @description
 */
@Data
public class YunYingDaiBanResult {
    private String processInstanceId;
    private String businessKey;
    private String processModelType;
    private Integer processStatus;
    private Long currentAssignerId;
    private String projCode;
    private String projName;
    private String contractCode;
    private Long bizDeptId;
    private Long projSponsorUserId;
    private String leaseTypes;
    private String projSponsorUserName;
    private String currentAssignerName;
    private String bizDeptName;

}
