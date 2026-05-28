package cn.zswltech.mithras.dto.liquiditymanage.liquidityIndex;

import cn.zswltech.mithras.dto.liquiditymanage.base.LiquidityColorVo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;
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
