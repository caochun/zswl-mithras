package cn.zswltech.mithras.dto.payment.lib;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;

/**
 * @description payment_policy_info
 * @author zhaozhengkang
 * @date 2022-09-13
 */
@Data
@ApiModel("payment_policy_info编辑flag-请求体")
public class PaymentPolicyInfoModifyFlagREQ {


    /**
     *
     */
    @ApiModelProperty("id")
    private Long id;

    @ApiModelProperty("0 隐藏勾选框/1 尚未购买保险/ 2 无需购买保险")
   private Integer flag;

}
