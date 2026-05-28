package cn.zswltech.mithras.dto.riskcontrol;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2023/8/18 16:14
 */
@Data
@ApiModel("参与计算客户明细")
public class ClientDetail {
    @ApiModelProperty(value = "客户id")
    private Long clientId;
    @ApiModelProperty(value = "客户名称")
    private String clientName;
    @ApiModelProperty(value = "剩余本金")
    private Long remainingPrincipal;
    @ApiModelProperty(value = "保证金")
    private Long deposit;

    @ApiModelProperty(value = "客户类型")
    private String clientType;
    @ApiModelProperty(value = "境内or境外")
    private String domesticOrAbroad;
}
