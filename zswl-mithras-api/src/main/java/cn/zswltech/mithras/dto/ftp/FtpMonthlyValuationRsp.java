package cn.zswltech.mithras.dto.ftp;
import cn.zswltech.mithras.dto.ListBaseRSP;
import lombok.Data;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
/**
 * @description 月度计价指导
 * @author zhaozhengkang
 * @date 2023-01-10
 */
@Data
@ApiModel("月度计价指导列表-返回体")
public class FtpMonthlyValuationRsp extends ListBaseRSP {
    @ApiModelProperty(value = "所属指引id")
    private Long guidanceId;

    @ApiModelProperty(value = "期限")
    private String crditTerm;

    @ApiModelProperty(value = "融资成本")
    private Integer financingCost;

    @ApiModelProperty(value = "担保成本")
    private Integer guaranteeCost;

    @ApiModelProperty(value = "成本小计")
    private Integer subtotalCost;

    @ApiModelProperty(value = "国股银票转贴现利率")
    private Integer discountRate;

    @ApiModelProperty(value = "权重1")
    private Integer discountRateWeight;

    @ApiModelProperty(value = "1年期shibor利率")
    private Integer shiborRate;

    @ApiModelProperty(value = "权重2")
    private Integer shiborRateWeight;

    @ApiModelProperty(value = "同期lpr利率")
    private Integer lprRate;

    @ApiModelProperty(value = "权重3")
    private Integer lprRateWeight;

    @ApiModelProperty(value = "融资成本趋势")
    private Integer financeCostTrends;

    @ApiModelProperty(value = "权重4")
    private Integer financeCostTrendsWeight;

    @ApiModelProperty(value = "小计")
    private Integer subtotalRate;

    @ApiModelProperty(value = "调整后计价小计")
    private Integer subtotalAdjustmentValuation;

    @ApiModelProperty(value = "鼓励介入类")
    private Integer encourageValuation;

    @ApiModelProperty(value = "适度类")
    private Integer moderateSupportValuation;

    @ApiModelProperty(value = "谨慎支持类")
    private Integer cautiousValuation;

    @ApiModelProperty(value = "国有/上市公司")
    private Integer stateOwnListedValuation;

    @ApiModelProperty(value = "其他类")
    private Integer otherValuation;
}
