package cn.zswltech.mithras.dto.liquiditymanage.liquidityindex;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;


/**
 * LiquidityIndexDetailRSP
 *
 * @author chenyifei
 * @since 2024/12/16
 */
@Data
@ApiModel(value = "流动性指标返回体")
public class LiquidityBoardDetailSumRSP {

    @ApiModelProperty(value = "列表")
    private List<LiquidityBoardDetailRSP> list;

    @ApiModelProperty(value = "合计")
    private LiquidityBoardDetailRSP sum;

}
