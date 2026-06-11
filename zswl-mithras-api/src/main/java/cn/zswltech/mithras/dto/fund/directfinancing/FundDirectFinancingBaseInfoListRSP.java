package cn.zswltech.mithras.dto.fund.directfinancing;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @author zhaozhengkang
 * @description 直接融资-详情信息
 * @date 2023-06-17
 */
@Data
@ApiModel("直接融资-详情信息列表-返回体")
public class FundDirectFinancingBaseInfoListRSP {

    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "产品名称")
    private String productName;

    @ApiModelProperty(value = "承销商")
    private String consignee;

    @ApiModelProperty(value = "融资编号")
    private String financingCode;

    @ApiModelProperty(value = "融资金额")
    private Long financingAmount;

    @ApiModelProperty(value = "剩余金额")
    private Long remainingAmount;

    @ApiModelProperty(value = "项目类型")
    private String directFinancingType;

    @ApiModelProperty(value = "加权成本（票面加权平均利率）")
    private Long averageCouponRate;

    @ApiModelProperty(value = "成立日期（存续时间起）")
    private LocalDate durationFrom;

    @ApiModelProperty(value = "创建人")
    private String createByName;

    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createTime;

    @ApiModelProperty(value = "更新时间")
    private LocalDateTime updateTime;

    @ApiModelProperty("是否已作废")
    private Boolean obsolete;

    @ApiModelProperty("融资状态")
    private String financingStatus;

    @ApiModelProperty("综合融资成本")
    private Long comprehensiveFinancingCost;

    @ApiModelProperty(value = "起息日")
    private LocalDate carryInterestTime;

    @ApiModelProperty(value = "到期日")
    private LocalDate durationTime;
}
