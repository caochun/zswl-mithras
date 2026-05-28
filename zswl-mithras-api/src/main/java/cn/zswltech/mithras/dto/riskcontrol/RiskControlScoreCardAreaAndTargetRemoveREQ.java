package cn.zswltech.mithras.dto.riskcontrol;
import lombok.Data;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotNull;
/**
 * @description risk_control_score_card_area_and_target
 * @author vico
 * @date 2023-03-02
 */
@Data
@ApiModel("risk_control_score_card_area_and_target删除-请求体")
public class RiskControlScoreCardAreaAndTargetRemoveREQ {

    @NotNull
    @ApiModelProperty("id")
    private Long id;

}
