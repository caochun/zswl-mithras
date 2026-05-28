package cn.zswltech.mithras.dto.fund;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author zhaozhengkang
 * @description fund_guarantee_agency
 * @date 2022-12-13
 */
@Data
@ApiModel("fund_guarantee_agency列表-返回体")
public class FundGuaranteeAgencyListRSP {
    @ApiModelProperty(value = "id")
    private Long id;
    @ApiModelProperty(value = "担保机构名称")
    private String guaranteeAgencyName;
    @ApiModelProperty(value = "担保机构编号")
    private String guaranteeAgencyCode;
    @ApiModelProperty(value = "总担保额度")
    private Long totalGuaranteeLimit;
    @ApiModelProperty(value = "已使用担保额度")
    private Long usedGuaranteeLimit;
    @ApiModelProperty(value = "剩余担保额度")
    private Long remainingGuaranteeLimit;
    @ApiModelProperty(value = "担保期限")
    private String guaranteePeriod;
    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createTime;
    @ApiModelProperty(value = "更新时间")
    private LocalDateTime updateTime;
    @ApiModelProperty("创建人id")
    private Long createBy;
    @ApiModelProperty("创建人name")
    private String createByName;
}
