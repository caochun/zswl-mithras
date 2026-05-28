package cn.zswltech.mithras.api.payment.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author dingqi
 * @date 2023/12/11
 * @description
 */
@Data
public class PaymentActualDetailAddReq {
    @NotNull(message = "付款id不能为空")
    @ApiModelProperty(value = "付款id")
    private Long paymentId;

    @NotBlank(message = "付款方式不能为空")
    @ApiModelProperty(value = "付款方式")
    private String paymentMethod;

    @NotNull(message = "实付金额不能为空")
    @ApiModelProperty(value = "实付金额")
    private Long paidInAmount;

    @NotNull(message = "实付日期不能为空")
    @ApiModelProperty(value = "实付日期")
    private String paidInDate;

    @NotNull(message = "我方账户id不能为空")
    @ApiModelProperty(value = "我方账户id")
    private Long ourAccountId;

    @NotBlank(message = "对方账户名不能为空")
    @ApiModelProperty(value = "对方账户名")
    private String oppositeAccountName;

    @NotBlank(message = "对方银行账号不能为空")
    @ApiModelProperty(value = "对方银行账号")
    private String oppositeAccountNo;

    @NotBlank(message = "对方账户开户行不能为空")
    @ApiModelProperty(value = "对方账户开户行")
    private String oppositeAccountBank;

    @NotBlank(message = "资金来源不能为空")
    @ApiModelProperty(value = "资金来源")
    private String capitalSource;

    @ApiModelProperty(value = "融资编号")
    private String financingCode;
}
