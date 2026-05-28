package cn.zswltech.mithras.dto.riskcontrol.opinion;
import lombok.Data;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotNull;
/**
 * @description 风控舆情监控
 * @author vico
 * @date 2023-03-09
 */
@Data
@ApiModel("风控舆情监控删除-请求体")
public class RiskControlOpinionMonitorRemoveREQ {

    @NotNull
    @ApiModelProperty("id")
    private Long id;

}
