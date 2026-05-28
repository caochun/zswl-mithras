package cn.zswltech.mithras.dto.projpricing.cashflowplan;

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
public class ProjPricingCashFlowPlanListCompareRSP extends ListBaseRSP {
    @ApiModelProperty("定价数据")
    private ProjPricingCashFlowPlanListRSP pricingDate;

    @ApiModelProperty("项目评审会会议数据")
    private ProjPricingCashFlowPlanListRSP meetMinuteDate;

    @ApiModelProperty("比对结果 true 不一致 false 一致")
    private Boolean compareResult;

}
