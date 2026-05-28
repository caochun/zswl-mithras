package cn.zswltech.mithras.dto.monthly;

import cn.zswltech.mithras.dto.PageReq;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
public class MonthlyCostRSP extends TabBaseRSP {

    @ApiModelProperty("id序列号")
    private Long id;

    @ApiModelProperty("融资id")
    private Long financingId;

    @ApiModelProperty("融资编号")
    private String financingCode;

    @ApiModelProperty("月份")
    private String yearAndMonth;

    @ApiModelProperty("融资渠道")
    private String organizationName;

    @ApiModelProperty("融资金额(元)")
    private Long financingAmount;

    @ApiModelProperty("融资余额(元)")
    private Long remainingAmount;

    @ApiModelProperty("业务类型")
    private String businessType;

    @ApiModelProperty("融资利率")
    private Integer financingRate;

    @ApiModelProperty("日利率")
    private Integer dailyRate;

    @ApiModelProperty("累计计提资金成本")
    private Long totalCapitalCost;

    @ApiModelProperty("累计计提资金成本税后")
    private Long totalCapitalCostAfterTax;

    @ApiModelProperty("当期应付利息")
    private Long termCapitalCost;

    @ApiModelProperty("当期应付利息（税后）")
    private Long termCapitalCostAfterTax;

    @ApiModelProperty("质押资产类型")
    private String propertyType;

    @TableField("当日应付利息")
    private Long financingCost;

    @ApiModelProperty(value = "起息日")
    private LocalDate valueDate;

    @ApiModelProperty("质押资产类型展示")
    private String propertyTypeDisplay;

    @ApiModelProperty("借款性质")
    private String loanProperty;

    @ApiModelProperty("收入是否确认")
    private Integer isConfirmed;

    @ApiModelProperty("钆差金额")
    private Long financingCostDiff;

    @ApiModelProperty("期初计提利息余额")
    private Long beginOfPeriodInterestBalance;

    @ApiModelProperty("期末计提利息余额")
    private Long endOfPeriodInterestBalance;
}
