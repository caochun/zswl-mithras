package cn.zswltech.mithras.dto.fund;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author zhaozhengkang
 * @description fund_guarantee_agency
 * @date 2022-12-13
 */
@Data
@ApiModel("fund_guarantee_agency新增-请求体")
public class FundGuaranteeAgencyAddREQ {
    @ApiModelProperty(value = "担保机构名称")
    @NotNull
    private String guaranteeAgencyName;

    @ApiModelProperty(value = "统一社会信用代码")
    @NotNull
    private String uscCode;

}
