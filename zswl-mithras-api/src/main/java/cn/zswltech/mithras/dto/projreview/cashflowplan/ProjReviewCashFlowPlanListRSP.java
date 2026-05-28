package cn.zswltech.mithras.dto.projreview.cashflowplan;

import cn.zswltech.mithras.dto.ListBaseRSP;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author dingqi
 * @date 2022/8/1
 * @description
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel("获取现金流计划表-返回体")
public class ProjReviewCashFlowPlanListRSP extends ListBaseRSP {
    @ApiModelProperty("日期")
    private String date;

    @ApiModelProperty("期项")
    private Integer phase;

    @ApiModelProperty("租金(租赁、转租赁)/应收保理款(保理)/回收款(债权转让)，单位：毫厘")
    private Long rent;

    @ApiModelProperty("现金流金额，单位：毫厘")
    private Long cashFlowAmount;

    @ApiModelProperty("本金，单位：毫厘")
    private Long principal;

    @ApiModelProperty("利息，单位：毫厘")
    private Long interest;

    @ApiModelProperty("剩余本金，单位：毫厘")
    private Long remainingPrincipal;
}
