package cn.zswltech.mithras.dto.dashboard;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@Data
public class DashboardProjectPlanListRSP {
    @ApiModelProperty("部门id")
    private Long bizDeptId;
    @ApiModelProperty("部门名称")
    private String bizDeptName;
    @ApiModelProperty("项目名称")
    private String projName;
    @ApiModelProperty("项目类型 - 租赁类型，保理类型，转让租赁类型")
    private String leaseType;
    @ApiModelProperty("合同id")
    private Long contractId;
    @ApiModelProperty("合同编号")
    private String contractCode;
    @ApiModelProperty("业务类型")
    private String bizType;
    @ApiModelProperty("业务类型display")
    private String bizTypeDisplay;
    @ApiModelProperty("合同金额")
    private ValueUnitDTO contractAmount;
    @ApiModelProperty("手续费")
    private ValueUnitDTO commission;
    @ApiModelProperty("保证金")
    private ValueUnitDTO earnestMoney;
    @ApiModelProperty("irr")
    private ValueUnitDTO irr;
    @ApiModelProperty("投放金额")
    private ValueUnitDTO actualPayAmount;
    @ApiModelProperty("客户id")
    private Long clientId;
    @ApiModelProperty("客户名称")
    private String clientName;
    @ApiModelProperty("项目主办")
    private Long projSponsorUserId;
    @ApiModelProperty("项目主办名称")
    private String projSponsorUserName;
    @ApiModelProperty("项目类型display")
    private String projectClassifyDisplay;
    @ApiModelProperty("省份")
    private String provinceDisplay;
    @ApiModelProperty("市")
    private String cityDisplay;
    @ApiModelProperty("地区")
    private String districtDisplay;
    @ApiModelProperty("合同期限")
    private ValueUnitDTO contractLimitYear;
    @ApiModelProperty("投放日期")
    private LocalDate actualPayDate;
    private Long paymentId;











}
