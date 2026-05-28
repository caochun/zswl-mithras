package cn.zswltech.mithras.dto.fund.receiptrepay;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author zhaozhengkang
 * @description 收付款
 * @date 2023-02-20
 */
@Data
@ApiModel("收付款列表-返回体")
public class FundReceiptRepayBaseInfoListRSP {

    @ApiModelProperty(value = "主键")
    private Long id;

    @ApiModelProperty(value = "融资编号")
    private String financingCode;

    @ApiModelProperty(value = "收付款编号")
    private String receiptRepayCode;

    @ApiModelProperty(value = "融资渠道")
    private String financingOrgName;

    @ApiModelProperty(value = "融资业务类型")
    private String financingBizType;
    @ApiModelProperty(value = "融资类型")
    private String financingType;

    @ApiModelProperty(value = "融资id")
    private Long financingId;


    @ApiModelProperty(value = "融资金额")
    private Long financingAmount;
//    @ApiModelProperty(value = "已还本金")
//    private Long repayPrincipal;
//    @ApiModelProperty(value = "已还利息")
//    private Long repayInterest;
//    @ApiModelProperty(value = "一年内到期本金")
//    private Long oneYearPrincipal;

    @ApiModelProperty(value = "本月待还金额")
    private Long monthRepayAmount;
    @ApiModelProperty(value = "本月应还本金")
    private Long monthRepayPrincipal;
    @ApiModelProperty(value = "本月应还利息")
    private Long monthRepayInterest;

    @ApiModelProperty(value = "收付款状态")
    private String receiptRepayState;
    @ApiModelProperty(value = "审批状态")
    private String processState;
    @ApiModelProperty(value = "创建人")
    private String createByName;

    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createTime;
    @ApiModelProperty(value = "更新时间")
    private LocalDateTime updateTime;

}
