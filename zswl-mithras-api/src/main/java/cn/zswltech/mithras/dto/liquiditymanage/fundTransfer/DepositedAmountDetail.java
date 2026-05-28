package cn.zswltech.mithras.dto.liquiditymanage.fundTransfer;

import cn.zswltech.mithras.dto.dashboard.ValueUnitDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


/**
 * AccountBalanceListREQ
 *
 * @author zhouning
 * @since 2024/12/24
 */
@Data
public class DepositedAmountDetail {

    @ApiModelProperty(value = "沉淀时间")
    private String settingTime;

    @ApiModelProperty("占比")
    private ValueUnitDTO payAmount;

    @ApiModelProperty("沉淀金额")
    private Long depositedAmount;

}
