package cn.zswltech.mithras.dashboard.mapper.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * @author dingqi
 * @date 2024/6/17
 * @description
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class DashboardProjectBasicResult extends DashboardDeptUserBasicResult {
    private Long mainId;
    private Long creditAmount;
    private String projName;
    private Long clientId;
    private String clientName;
    private LocalDateTime createTime;
    private Integer workdaysOnStage;
    private String riskControlIndustryClassifyCode;
    private String industryCode;
    private String industryDisplay;
    private String bizTypeCode;
    private String leaseTypeCode;
    private LocalDateTime processInstanceStartTime;
    private LocalDateTime processInstanceEndTime;
    private Integer processWorkdays;
    private Integer processInstanceStatus;
    private String lesseeInfoJson;
    private String guaranteeInfoJson;
}
