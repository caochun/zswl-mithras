package cn.zswltech.mithras.dto.projestablish.baseinfo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author junke
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("客户存量风险敝口获取-返回体")
public class ClientStockRiskExposureRSP {

    @ApiModelProperty("客户id")
    private Long clientId;

    @ApiModelProperty("存量风险敝口")
    private Long stockRiskExposure;
}
