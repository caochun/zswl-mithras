package cn.zswltech.mithras.fund.application.liquiditymanage.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * @author bigbear
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RepayPrincipalInterestDto {

    @ApiModelProperty(value = "融资类型")
    private String type;

    @ApiModelProperty(value = "融资id")
    private Long financingId;

    @ApiModelProperty(value = "融资机构名称")
    private String organizationName;

    @ApiModelProperty(value = "融资编号")
    private String financingCode;

    @ApiModelProperty(value = "融资金额")
    private String financingAmount;

    @ApiModelProperty(value = "本期到期日")
    private LocalDate dueDate;

    @ApiModelProperty(value = "本期应还金额")
    private Long shouldPayAmount;

    @ApiModelProperty(value = "本期应还本金")
    private Long shouldPayPrincipal;

    @ApiModelProperty(value = "本期应还利息")
    private Long shouldPayInterest;
}