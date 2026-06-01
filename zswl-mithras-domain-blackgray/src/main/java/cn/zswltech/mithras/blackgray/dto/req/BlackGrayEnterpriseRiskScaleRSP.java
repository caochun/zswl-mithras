package cn.zswltech.mithras.blackgray.dto.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @description 黑灰名单库
 * @author
 * @date 2023-11-28
 */
@Data
@ApiModel("黑灰名单风险规模-返回体")
public class BlackGrayEnterpriseRiskScaleRSP {


    @ApiModelProperty(value = "风险暴露", required = true, example = "111.22")
    private BigDecimal riskExposure;


}
