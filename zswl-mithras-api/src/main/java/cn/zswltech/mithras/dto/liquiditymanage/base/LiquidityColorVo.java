package cn.zswltech.mithras.dto.liquiditymanage.base;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * liquidityColorVo
 *
 * @author chenyifei
 * @since 2024/12/16
 */
@Data
@ApiModel(value = "流动性管理返回参数")
@AllArgsConstructor
@NoArgsConstructor
public class LiquidityColorVo {

    @ApiModelProperty(value = "值")
    private BigDecimal value;

    /**
     * {@link LiquidityColorEnum#name()}
     */
    @ApiModelProperty(value = "颜色")
    private String color;

    @ApiModelProperty(value = "层级")
    private Integer level;

    public LiquidityColorVo(BigDecimal value, String color) {
        this.value = value;
        this.color = color;
    }
}
