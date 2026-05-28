package cn.zswltech.mithras.dto.liquiditymanage.dayReport;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author bigbear
 * @date 2024/12/13 16:43
 * @className RentIncomeListRSP
 * @description
 */
@Data
@ApiModel(value = "租金收入列表响应参数")
public class RentIncomeListRSP {

    @ApiModelProperty(value = "承租人id")
    private Long tenantId;

    @ApiModelProperty(value = "承租人姓名")
    private String tenantName;

    @ApiModelProperty(value = "合同id")
    private Long contractId;

    @ApiModelProperty(value = "合同编号")
    private String contractCode;

    @ApiModelProperty(value = "本期到期日")
    private String expireDate;

    @ApiModelProperty(value = "本期应还金额")
    private String shouldPayAmount;

    @ApiModelProperty(value = "已还金额")
    private String paidAmount;

    @ApiModelProperty(value = "未还金额")
    private String unpaidAmount;
}
