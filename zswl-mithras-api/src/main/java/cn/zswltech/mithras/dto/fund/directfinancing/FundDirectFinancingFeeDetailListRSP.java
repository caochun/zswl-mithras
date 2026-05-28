package cn.zswltech.mithras.dto.fund.directfinancing;

import cn.zswltech.mithras.api.common.PageR;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;

/**
 * @author zhaozhengkang
 * @description 直接融资-费用明细
 * @date 2023-06-17
 */
@Data
@ApiModel("直接融资-费用明细列表-返回体")
public class FundDirectFinancingFeeDetailListRSP {

    @ApiModelProperty(value = "融资金额")
    private Long financingAmount;

    @ApiModelProperty(value = "票面加权平均利率")
    private Long averageCouponRate;

    @ApiModelProperty(value = "费用合计")
    private Long totalFee;

    /**
     * 起息日
     */
    @TableField("起息日")
    private LocalDate carryInterestTime;

    /**
     * 融资期限（月）
     */
    @ApiModelProperty("融资期限（月）")
    private Integer financingMonth;

    private Integer ftpYieldRate;


    /**
     * 融资状态
     */
    @ApiModelProperty("融资状态")
    private String financingStatus;

    /**
     * 综合融资成本
     */
    @ApiModelProperty("综合融资成本")
    private Long comprehensiveFinancingCost;

    /**
     * 还款方式
     */
    @ApiModelProperty("还款方式")
    private String repayWay;

    /**
     * 还款频率
     */
    @ApiModelProperty("还款频率")
    private String repayFrequency;

    /**
     * 计算日
     */
    @ApiModelProperty("计算日")
    private Integer calculateDay;

    @ApiModelProperty(value = "费用明细")
    private PageR<FundDirectFinancingFeeDetailRSP> page;

}
