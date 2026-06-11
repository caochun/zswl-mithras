package cn.zswltech.mithras.dto.liquiditymanage.liquidityindex;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * LiquidityIndexDetailREQ
 *
 * @author chenyifei
 * @since 2024/12/16
 */
@Data
@ApiModel(value = "流动性指标请求体")
public class LiquidityIndexDetailREQ {

    /**
     * 预测区间-开始
     */
    @ApiModelProperty(value = "预测区间-开始")
    @NotNull(message = "不得为空")
    private LocalDate queryDateStart;

    /**
     * 预测区间-结束
     */
    @ApiModelProperty(value = "预测区间-结束")
    @NotNull(message = "不得为空")
    private LocalDate queryDateEnd;




}
