package cn.zswltech.mithras.dto.dashboard;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * @author dingqi
 * @date 2024/6/15
 * @description
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class DashboardProjectBasicRSP extends DashboardProjectDeptUserBasicRSP {
    @ApiModelProperty("主数据ID")
    private Long mainId;
    @ApiModelProperty("合同ID")
    private Long contractId;
    @ApiModelProperty("客户ID")
    private Long clientId;
    @ApiModelProperty("客户名称")
    private String clientName;
    @ApiModelProperty("项目名称")
    private String projName;
    @ApiModelProperty("当前阶段停留时长")
    private ValueUnitDTO lengthOfStay;
    @ApiModelProperty("授信金额")
    private ValueUnitDTO creditAmount;
    @ApiModelProperty("风控行业分类code")
    private String riskControlIndustryClassifyCode;
    @ApiModelProperty("风控行业分类display")
    private String riskControlIndustryClassifyDisplay;
    @ApiModelProperty("国标行业分类code")
    private String industryTypeCode;
    @ApiModelProperty("国标行业分类display")
    private String industryTypeDisplay;
    @ApiModelProperty("业务类型code")
    private String bizTypeCode;
    @ApiModelProperty("业务类型display")
    private String bizTypeDisplay;
    @ApiModelProperty("业务模式code")
    private String leaseTypeCode;
    @ApiModelProperty("业务模式display")
    private String leaseTypeDisplay;
    @ApiModelProperty("承租人ID，多个用英文逗号分隔")
    private String lesseeIds;
    @ApiModelProperty("承租人名称，多个用英文逗号分隔")
    private String lesseeNames;
    @ApiModelProperty("担保人ID，多个用英文逗号分隔")
    private String guarantorIds;
    @ApiModelProperty("担保人名称，多个用英文逗号分隔")
    private String guarantorNames;
    @ApiModelProperty("申请时间")
    private LocalDateTime applyTime;
    @ApiModelProperty("审批耗时")
    private ValueUnitDTO approveElapsedTime;
}
