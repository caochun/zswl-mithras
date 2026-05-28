package cn.zswltech.mithras.dto.collection;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class CollectionFlowCenterBusinessCollectionSettleDetailRSP {

    @ApiModelProperty("ID")
    private Long id;

    @ApiModelProperty("收款类型")
    private String collectionType;

    @ApiModelProperty("实收日期")
    private LocalDate collectionDate;

    @ApiModelProperty("实收金额")
    private Long collectionAmount;

    @ApiModelProperty("本金")
    private Long principal;

    @ApiModelProperty("利息")
    private Long interest;

    @ApiModelProperty("罚息")
    private Long penaltyInterest;

    @ApiModelProperty(value = "票据code")
    private String billCode;

    /**
     * 票据金额
     */
    @ApiModelProperty(value = "票据金额")
    private Long billAmount;

    /**
     * 票据到期日期
     */
    @ApiModelProperty(value = "票据到期日期")
    private LocalDate billExpireDate;

    @ApiModelProperty("票据买入价")
    private Long billBuyRate;

    @ApiModelProperty("核销方式")
    private String writeOffType;

    @ApiModelProperty("核销状态")
    private String writeOffStatus;

    @ApiModelProperty("更新时间")
    private LocalDateTime updateTime;

    @ApiModelProperty("创建人id")
    private Long updateBy;

    @ApiModelProperty("创建人名称")
    private String updateByName;

    @ApiModelProperty("交易明细编号")
    private String bankDetailNo;


    @ApiModelProperty(value = "deduction_margin_base_id")
    private Long deductionMarginBaseId;
    //抵扣保证金编号
    @ApiModelProperty(value = "deduction_margin_base_code")
    private String deductionMarginBaseCode;
    @ApiModelProperty("合同编号")
    private String contractCode;

}
