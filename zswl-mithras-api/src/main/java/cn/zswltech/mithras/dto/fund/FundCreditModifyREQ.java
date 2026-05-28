package cn.zswltech.mithras.dto.fund;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

/**
 * @author zhaozhengkang
 * @description fund_credit
 * @date 2022-12-13
 */
@Data
@ApiModel("fund_credit编辑-请求体")
public class FundCreditModifyREQ {
    @ApiModelProperty(value = "id")
    private Long id;
    @ApiModelProperty(value = "资金用途")
    private String fundUsage;
    @ApiModelProperty(value = "增信方式")
    private List<String> enhanceCreditMethod;
    @ApiModelProperty(value = "额度是否可循环")
    private Integer recyclable;
    @ApiModelProperty(value = "担保明细")
    private List<FundCreditGuaranteeDetailDto> guaranteeDetail;
    @ApiModelProperty(value = "授信生效时间")
    private LocalDate effectiveDateFrom;
    @ApiModelProperty(value = "授信生效时间")
    private LocalDate effectiveDateTo;
    @ApiModelProperty(value = "备注")
    private String remark;
    @ApiModelProperty(value = "总授信额度")
    private Long totalCreditLimit;

}
