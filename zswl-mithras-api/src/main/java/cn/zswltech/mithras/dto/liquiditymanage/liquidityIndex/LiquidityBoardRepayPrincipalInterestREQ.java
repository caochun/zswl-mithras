package cn.zswltech.mithras.dto.liquiditymanage.liquidityIndex;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * @author bigbear
 * @date 2025/2/21 14:49
 * @description
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class LiquidityBoardRepayPrincipalInterestREQ extends LiquidityIndexDetailREQ {

    @ApiModelProperty(value = "本期到期日-开始")
    private String expireDateFrom;

    @ApiModelProperty(value = "本期到期日-结束")
    private String expireDateTo;
}
