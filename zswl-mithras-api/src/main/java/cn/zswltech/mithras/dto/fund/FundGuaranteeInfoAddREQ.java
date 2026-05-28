package cn.zswltech.mithras.dto.fund;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * @author zhaozhengkang
 * @description fund_guarantee_info
 * @date 2022-12-13
 */
@Data
@ApiModel("担保信息新增-请求体")
public class FundGuaranteeInfoAddREQ {
    @ApiModelProperty(value = "担保机构id")
    @NotNull
    private Long agencyId;
    @ApiModelProperty(value = "总担保额度")
    private Long totalGuaranteeLimit;
    @ApiModelProperty(value = "担保生效时间from")
    private LocalDate effectiveTimeFrom;
    @ApiModelProperty(value = "担保生效时间to")
    private LocalDate effectiveTimeTo;
    @ApiModelProperty(value = "额度是否可循环")
    private Integer recyclable;
    @ApiModelProperty(value = "备注")
    private String remark;
}
