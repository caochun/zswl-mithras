package cn.zswltech.mithras.api.payment.writeoff;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2022/8/16 13:58
 */
@ApiModel("付款记录DTO")
@Data
public class ActualDetailListRsp {

    @ApiModelProperty("付款Id")
    private Long paymentId;

    @ApiModelProperty("合同id")
    private Long contractId;

    @ApiModelProperty("应付")
    private Long payableAmount;

    @ApiModelProperty("已付")
    private Long paidAmount;

    @ApiModelProperty("默认对方账户名称")
    private String defaultOppositeAccountName;

    @ApiModelProperty("默认对方账号")
    private String defaultOppositeAccount;

    @ApiModelProperty("默认对方开户行")
    private String defaultOppositeAccountBank;

    @ApiModelProperty("待付款")
    private Long obligation;

    @ApiModelProperty("已核销人ids")
    private List<Long> writeOffUserIds;

    @ApiModelProperty("已核销人")
    private List<String> writeOffUserName;

    @ApiModelProperty("已核销核销记录")
    private List<ActualDetailDto> actualDetails;

    @ApiModelProperty("已确认核销记录")
    private List<ActualDetailDto> confirmedActualDetails;

    @ApiModelProperty("未确认核销记录")
    private List<ActualDetailDto> unconfirmedActualDetails;

    @ApiModelProperty("【已确认】+【未确认】的 ∑实付金额是否等于申请付款金额")
    private Boolean amountIsSame;
}
