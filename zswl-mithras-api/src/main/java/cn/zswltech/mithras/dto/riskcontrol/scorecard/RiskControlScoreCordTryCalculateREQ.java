package cn.zswltech.mithras.dto.riskcontrol.scorecard;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @ClassName AreaSearchRSP
 * @Description TODO
 * @Author jackerhe
 * @Date 2023/3/2 4:03 下午
 * @Version 1.0
 **/
@Data
public class RiskControlScoreCordTryCalculateREQ {

    @ApiModelProperty(value = "地区id")
    @NotNull(message = "地区id不能为空")
    private Long areaId;

    @ApiModelProperty(value = "评分卡id")
    @NotNull(message = "评分卡id不能为空")
    private Long cardId;

}
