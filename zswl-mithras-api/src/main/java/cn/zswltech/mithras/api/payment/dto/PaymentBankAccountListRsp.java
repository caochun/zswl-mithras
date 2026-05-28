package cn.zswltech.mithras.api.payment.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author dingqi
 * @date 2022/9/15
 * @description
 */
@Data
@ApiModel("付款-对方账户列表-返回体")
public class PaymentBankAccountListRsp {
    @ApiModelProperty("账户名称")
    private String accountName;
    @ApiModelProperty("账号")
    private String accountNumber;
    @ApiModelProperty("开户行")
    private String accountBank;
}
