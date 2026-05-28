package cn.zswltech.mithras.dto.riskcontrol.scorecard;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @ClassName RiskControlScoreCordAreaType
 * @Description 城市类型
 * @Author jackerhe
 * @Date 2023/3/2 3:47 下午
 * @Version 1.0
 **/
@Data
public class RiskControlScoreCordAreaType {
    @ApiModelProperty(value = "城市类型 AreaTypeEnum")
    private String areaType;

    @ApiModelProperty(value ="最小值")
    @NotNull(message = "最小值不能为空")
    private Long min;

    @NotNull(message = "最da值不能为空")
    @ApiModelProperty(value = "最大值")
    private Long max;

}
