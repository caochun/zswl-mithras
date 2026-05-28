package cn.zswltech.mithras.dto.riskcontrol.opinion;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;
import java.util.Map;

/**
 * @author vico
 * @description 风控舆情监控
 * @date 2023-03-09
 */
@Data
@ApiModel("风控舆情监控列表-返回体")
public class RiskWarnMonitorQuantityChangeRSP {

    @ApiModelProperty("类型 RiskOpinionWarnCordType")
    private String cardCode;

    @ApiModelProperty("名称")
    private String cardCodeName;

    @ApiModelProperty("时间 数量")
    private Map<LocalDate, Integer> cardDetail;



}
