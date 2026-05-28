package cn.zswltech.mithras.dto.fund;

import cn.zswltech.mithras.dto.PageReq;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;

/**
 * @author zhaozhengkang
 * @description fund_guarantee_agency
 * @date 2022-12-13
 */
@Data
@ApiModel("fund_guarantee_agency列表-请求体")
public class FundGuaranteeAgencyListREQ extends PageReq {
    @ApiModelProperty(value = "担保机构名称")
    private String guaranteeAgencyName;

    @ApiModelProperty(value = "担保额度from")
    private String guaranteeLimitFrom;

    @ApiModelProperty(value = "担保额度to")
    private String guaranteeLimitTo;

    @ApiModelProperty(value = "担保日期from")
    private LocalDate guaranteeDateFrom;

    @ApiModelProperty(value = "担保日期to")
    private LocalDate guaranteeDateTo;

    @ApiModelProperty(value = "创建人id")
    private Long createBy;
}
