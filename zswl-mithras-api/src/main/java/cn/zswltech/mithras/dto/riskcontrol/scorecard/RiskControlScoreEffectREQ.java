package cn.zswltech.mithras.dto.riskcontrol.scorecard;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @ClassName RiskContralScoreCordImportREQ
 * @Description TODO
 * @Author jackerhe
 * @Date 2023/2/27 2:04 下午
 * @Version 1.0
 **/
@Data
public class RiskControlScoreEffectREQ {

    @ApiModelProperty("评分卡id")
    @NotNull(message = "评分卡id")
    private Long id;

}
