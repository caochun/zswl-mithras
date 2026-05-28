package cn.zswltech.mithras.dto.dashboard.boss;

import cn.zswltech.mithras.dto.dashboard.ValueUnitDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author yangxiong
 * @date 2024/5/15/16:11
 * @description
 */
@Data
public class AssetsOverviewDetailListRSP {

    @ApiModelProperty(value = "排名")
    private Integer rank;

    @ApiModelProperty(value = "数据聚合维度")
    private String dimensionality;

    @ApiModelProperty(value = "资产余额")
    private ValueUnitDTO assetsBalance;

    @ApiModelProperty(value = "资产余额（毫厘）")
    private Long assetsBalanceL;

    @ApiModelProperty(value = "资产占比")
    private ValueUnitDTO assetsProportion;

    @ApiModelProperty(value = "本年投放额")
    private ValueUnitDTO loanThisYear;

    @ApiModelProperty(value = "存量项目数")
    private Integer stockProjectQuantity;

    @ApiModelProperty(value = "存量客户数")
    private Integer stockClientQuantity;
}
