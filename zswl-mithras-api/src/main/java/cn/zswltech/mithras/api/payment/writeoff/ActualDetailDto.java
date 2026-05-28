package cn.zswltech.mithras.api.payment.writeoff;

import cn.zswltech.mithras.api.payment.dto.PaymentMaterialsListRsp;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2022/8/16 13:58
 */
@ApiModel("付款记录DTO")
@Data
public class ActualDetailDto {

    @ApiModelProperty("主键id")
    private Long id;

    @ApiModelProperty("序号")
    private String seqCode;

    @ApiModelProperty("所属支付申请id")
    private Long paymentId;

    @ApiModelProperty("客户id")
    private Long clientId;

    @ApiModelProperty("客户名称")
    private String clientName;

    @ApiModelProperty("所属合同id")
    private Long contractId;

    @ApiModelProperty("所属合同编号")
    private String contractCode;

    @ApiModelProperty("同步还是录入，显示‘财务系统’或者录入者名字")
    private String infoSource;

    @ApiModelProperty("付款方式名称")
    private String paymentMethod;

    @ApiModelProperty("付款方式")
    private String paymentMethodEnum;

    @ApiModelProperty("付款类型 1票据, 0其他")
    private Integer  paymentWay;

    @ApiModelProperty("实付日期")
    private LocalDate paidInDate;

    @ApiModelProperty("应付金额")
    private Long paidInAmount;

    @ApiModelProperty("已付金额")
    private Long paidAmount;

    @ApiModelProperty("附言")
    private String postscript;

    @ApiModelProperty("核销状态（确认状态）")
    private String writeOffStatus;
    @ApiModelProperty("附件名称")
    private String enclosureName;
    @ApiModelProperty("附件id")
    private Long enclosureId;

    @ApiModelProperty("附件材料")
    private List<PaymentMaterialsListRsp> materialsList;

    @ApiModelProperty("our_account_id")
    private Long ourAccountId;
    @ApiModelProperty("our_account_name")
    private String ourAccountName;
    @ApiModelProperty("our_account_number")
    private String ourAccountNumber;
    @ApiModelProperty("our_account_bank")
    private String ourAccountBank;

    @ApiModelProperty("opposite_account_id")
    private Long oppositeAccountId;
    @ApiModelProperty("opposite_account_name")
    private String oppositeAccountName;
    @ApiModelProperty("opposite_account_number")
    private String oppositeAccountNumber;
    @ApiModelProperty("opposite_account_bank")
    private String oppositeAccountBank;

    @ApiModelProperty("capital_source")
    private String capitalSource;

    @ApiModelProperty("资金来源")
    private String capitalSourceEnum;

    @ApiModelProperty("financing_code")
    private String financingCode;
}
