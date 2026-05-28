package cn.zswltech.mithras.dto.liquiditymanage.liquidityIndex;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author bigbear
 * @date 2025/2/20 19:47
 * @description
 */
@Data
@ApiModel(value = "流动性指标-还本付息响应体")
public class LiquidityBoardRepayPrincipalInterestRSP {

    @ApiModelProperty(value = "融资机构")
    private List<String> organizationName;

    @ApiModelProperty(value = "融资编号")
    private String financingCode;

    @ApiModelProperty(value = "融资Id")
    private Long financingId;

    @ApiModelProperty(value = "本期到期日 format: yyyy-MM-dd")
    private String expireDate;

    @ApiModelProperty(value = "本期应还金额")
    private String shouldPayAmount;

    @ApiModelProperty(value = "本期应还本金")
    private String shouldPayPrincipal;

    @ApiModelProperty(value = "本期应还利息")
    private String shouldPayInterest;

    @ApiModelProperty(value = "本金流出账户")
    private String principalOutflowAccount;

    @ApiModelProperty(value = "本金流出账户性质")
    private String principalOutflowAccountProperty;

    @ApiModelProperty(value = "利息流出账户")
    private String interestOutflowAccount;

    @ApiModelProperty(value = "利息流出账户性质")
    private String interestOutflowAccountProperty;

}
