package cn.zswltech.mithras.api.payment.dto;

import cn.zswltech.mithras.dto.ListBaseRSP;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2022/8/15 16:33
 */
@ApiModel("付款计划dto")
@Data
public class PlanedDetailDto extends ListBaseRSP {
    /**
     * 所属支付id
     */
    @ApiModelProperty("所属支付id")
    @NotNull(message = "paymentId 为空")
    private Long paymentId;

    /**
     * 收款方客户id
     */
    @ApiModelProperty("收款方客户id")
    private Long payeeClientId;

    /**
     * 收款方客户名称
     */
    @ApiModelProperty("收款方客户名称")
    private String payeeClientName;

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

    /**
     * 支付方式
     */
    @ApiModelProperty("支付方式")
    private String paymentMethod;

    /**
     * 支付金额
     */
    @ApiModelProperty("支付金额")
    @NotNull(message = "支付金额为空")
    private Long paymentAmount;

    /**
     * 附言
     */
    @ApiModelProperty("附言")
    private String postscript;
}
