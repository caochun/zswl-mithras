package cn.zswltech.mithras.dto.fund.directfinancing;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2023/6/17 16:02
 */
@ApiModel("直接融资-修改融资方案-请求体")
@Data
public class FundDirectFinancingProgrammeModifyREQ {

    @ApiModelProperty(value = "直接融资id")
    @NotNull(message = "直接融资id不能为空")
    private Long financingId;

    @ApiModelProperty(value = "融资金额")
    @NotNull(message = "融资金额不能为空")
    private Long financingAmount;

//    @ApiModelProperty(value = "票面加权平均利率")
//    @NotNull(message = "票面加权平均利率不能为空")
//    private Long averageCouponRate;

    @TableField("起息日")
    private LocalDate carryInterestTime;

    @TableField("融资期限（月）")
    private Integer financingMonth;

    @TableField("还款方式")
    private String repayWay;

    @TableField("还款频率")
    private String repayFrequency;

    @TableField("计算日")
    private Integer calculateDay;
}
