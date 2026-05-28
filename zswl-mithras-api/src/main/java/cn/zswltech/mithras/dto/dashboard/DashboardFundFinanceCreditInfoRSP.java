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
public class DashboardFundFinanceCreditInfoRSP {
    @ApiModelProperty("授信ID")
    private Long id;
    @ApiModelProperty("授信编号")
    private String creditCode;
    @ApiModelProperty("机构名称")
    private String orgName;
    @ApiModelProperty("产品类型code")
    private String financingBizTypeCode;
    @ApiModelProperty("产品类型display")
    private String financingBizTypeDisplay;
    @ApiModelProperty("总授信额度")
    private ValueUnitDTO creditTotalAmount;
    @ApiModelProperty("已使用授信额度")
    private ValueUnitDTO creditUsedAmount;
    @ApiModelProperty("是否可循环，1-是，0-否")
    private Integer isCycle;
    @ApiModelProperty("授信到期日")
    private LocalDate deadline;
}
