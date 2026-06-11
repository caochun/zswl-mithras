package cn.zswltech.mithras.dto.liquiditymanage.fundtransfer;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


/**
 * FundTransferBankAccountListRSP
 *
 * @author zhouning
 * @since 2024/12/16
 */
@Data
@ApiModel(value = "我方账户列表-返回体")
public class FundTransferBankAccountListRSP {

    @ApiModelProperty("账户id")
    private Long id;

    @ApiModelProperty("账户名称")
    private String accountName;

    @ApiModelProperty("账户类型")
    private String accountType;

    @ApiModelProperty("银行账号")
    private String accountNumber;

    @ApiModelProperty("开户银行")
    private String accountBank;
}
