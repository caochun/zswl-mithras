package cn.zswltech.mithras.dto.liquiditymanage.fundtransfer;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;



/**
 * FundTransferBankAccountListREQ
 *
 * @author zhouning
 * @since 2024/12/16
 */
@Data
@ApiModel(value = "我方账户-列表-请求体")
public class FundTransferBankAccountListREQ {

    @ApiModelProperty("银行名称")
    private String bankName;
}
