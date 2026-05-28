package cn.zswltech.mithras.dto.riskcontrol.opinion;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author vico
 * @description 风控舆情监控
 * @date 2023-03-09
 */
@Data
@ApiModel("风控舆情监控列表-返回体")
public class RiskWarnMonitorTypeChangeRSP {

    @ApiModelProperty("类型")
    private String cardCode;

    @ApiModelProperty("名称")
    private String cardCodeName;

    @ApiModelProperty("数量")
    private Integer amount;

}
