package cn.zswltech.mithras.dto.dashboard;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


/**
 * @author dingqi
 * @date 2024/6/16
 * @description
 */
@Data
public class DashboardClientOverviewOverdueRSP {

    @ApiModelProperty(value = "客户ID")
    private Long clientId;

    @ApiModelProperty(value = "客户名称")
    private String clientName;

    @ApiModelProperty(value = "项目名称")
    private String projName;

    @ApiModelProperty(value = "合同ID")
    private Long contractId;

    @ApiModelProperty(value = "合同编号")
    private String contractCode;

    @ApiModelProperty(value = "逾期期项")
    private Integer phase;

    @ApiModelProperty(value = "逾期金额")
    private String overdueAmount;

    @ApiModelProperty(value = "罚息日利率")
    private String penaltyInterestRate;

    @ApiModelProperty(value = "罚息金额")
    private String penaltyInterestAmount;

    @ApiModelProperty(value = "业务类型")
    private String bizType;

    @ApiModelProperty(value = "业务模式")
    private String bizModel;

    @ApiModelProperty(value = "承租人")
    private String tenantry;

    @ApiModelProperty(value = "担保人")
    private String guarantorList;

    @ApiModelProperty("业务部门ID")
    private Long bizDeptId;

    @ApiModelProperty("业务部门名称")
    private String bizDeptName;

    @ApiModelProperty("项目主办ID")
    private Long projSponsorUserId;

    @ApiModelProperty("项目主办名称")
    private String projSponsorUserName;

    @ApiModelProperty("项目协办ID，多个用英文逗号分隔")
    private String projCosponsorUserIds;

    @ApiModelProperty("项目协办名称，多个用英文逗号分隔")
    private String projCosponsorUserNames;

    @ApiModelProperty(value = "罚息减免金额")
    private Long penaltyInterestReductionAmount;
}
