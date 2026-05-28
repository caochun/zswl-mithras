package cn.zswltech.mithras.dto.riskcontrol;
import lombok.Data;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotNull;
/**
 * @description risk_control_score_card_target
 * @author vico
 * @date 2023-02-27
 */
@Data
@ApiModel("评分卡指标-删除-请求体")
public class RiskControlScoreCardTargetRemoveREQ {

    @NotNull
    @ApiModelProperty("id")
    private Long id;

}
