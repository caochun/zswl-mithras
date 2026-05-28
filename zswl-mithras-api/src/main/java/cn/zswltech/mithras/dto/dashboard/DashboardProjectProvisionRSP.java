package cn.zswltech.mithras.dto.dashboard;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;

/**
 * @author dingqi
 * @date 2024/6/24
 * @description
 */
@Data
public class DashboardProjectProvisionRSP {
    @ApiModelProperty("客户ID")
    private Long clientId;
    @ApiModelProperty("客户名称")
    private String clientName;
    @ApiModelProperty("合同ID")
    private Long contractId;
    @ApiModelProperty("合同编号")
    private String contractCode;
    @ApiModelProperty("合同金额")
    private ValueUnitDTO contractAmount;
    @ApiModelProperty("剩余本金")
    private ValueUnitDTO principalBalance;
    @ApiModelProperty("保证金")
    private ValueUnitDTO earnest;
    @ApiModelProperty("风险敞口")
    private ValueUnitDTO stockRiskExposure;
    @ApiModelProperty("剩余期限（月）")
    private String remainingDurationMonths;
    @ApiModelProperty("剩余期限（年）")
    private String remainingDurationYears;
    @ApiModelProperty("拨备金额")
    private ValueUnitDTO provision;
    @ApiModelProperty("业务类型code")
    private String bizTypeCode;
    @ApiModelProperty("业务类型display")
    private String bizTypeDisplay;
    @ApiModelProperty("项目类型code")
    private String projectClassifyCode;
    @ApiModelProperty("项目类型display")
    private String projectClassifyDisplay;
    @ApiModelProperty("到期日")
    private LocalDate deadline;
    @ApiModelProperty("五级分类code")
    private String assetsClassifyCode;
    @ApiModelProperty("五级分类display")
    private String assetsClassifyDisplay;
    @ApiModelProperty("计提比例")
    private ValueUnitDTO provisionRate;
    @ApiModelProperty("拨备日期")
    private LocalDate provisionDate;
    @ApiModelProperty("业务部门ID")
    private Long bizDeptId;
    @ApiModelProperty("业务部门名称")
    private String bizDeptName;
}
