package cn.zswltech.mithras.guanbao.mapper.managereport;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @author dingqi
 * @date 2024/12/10
 * @description
 */
@Data
public class YeWuYunXingFenXiResult {
    private String processInstanceId;
    private String processModelType;
    private String leaseTypes;
    private String clientRiskControlIndustryClassify;
    private String projCode;
    private String projName;
    private String contractCode;
    private Long projAmount;
    private Long bizDeptId;
    private String bizDeptName;
    private Long projSponsorUserId;
    private String projSponsorUserName;
    private Integer processStatus;
    private String processStatusDisplay;
    private LocalDateTime processStartTime;
    private LocalDateTime processEndTime;
//    private String processEndTimeMonth;
//    private String processEndTimeYear;
    private LocalDate minPayDate;
//    private String minPayDateMonth;
    private Long actualPayAmount;
}
