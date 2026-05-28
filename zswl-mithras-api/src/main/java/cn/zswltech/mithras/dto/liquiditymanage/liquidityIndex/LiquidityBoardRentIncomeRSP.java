package cn.zswltech.mithras.dto.liquiditymanage.liquidityIndex;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author bigbear
 * @date 2025/2/20 19:54
 * @description
 */
@Data
public class LiquidityBoardRentIncomeRSP {

    @ApiModelProperty(value = "承租人Id")
    private Long tenantId;

    @ApiModelProperty(value = "承租人名称")
    private String tenantName;

    @ApiModelProperty(value = "合同编号")
    private String contractCode;

    @ApiModelProperty(value = "合同Id")
    private Long contractId;

    @ApiModelProperty(value = "本期到期日 format: yyyy-MM-dd")
    private String expireDate;

    @ApiModelProperty(value = "本期应还金额")
    private String shouldPayAmount;

    @ApiModelProperty(value = "流入账户")
    private String incomeAccount;

    @ApiModelProperty(value = "流入账户性质")
    private String incomeAccountProperty;
}
