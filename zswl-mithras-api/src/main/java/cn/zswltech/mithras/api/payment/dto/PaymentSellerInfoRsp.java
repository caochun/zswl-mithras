package cn.zswltech.mithras.api.payment.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel("卖方账号信息-出参")
@Data
public class PaymentSellerInfoRsp {

    /**
     * 对方账号
     */
    @ApiModelProperty("对方账号")
    private String oppositeAccount;

    /**
     * 对方账号名
     */
    @ApiModelProperty("对方账号名")
    private String oppositeAccountName;

    /**
     * 对方账号开户行
     */
    @ApiModelProperty("对方账号开户行")
    private String oppositeAccountBank;

}
