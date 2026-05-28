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
public class AccountDepositedAmountDetail extends DepositedAmountDetail{

    @ApiModelProperty(value = "银行名称")
    private String accountBank;

    @ApiModelProperty("银行账号")
    private String accountNumber;


}
