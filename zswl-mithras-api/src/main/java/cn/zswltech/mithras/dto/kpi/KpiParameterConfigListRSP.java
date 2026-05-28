package cn.zswltech.mithras.dto.kpi;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author dingqi
 * @date 2023/2/10
 * @description
 */
@Data
@ApiModel("绩效考核-参数设置列表-返回体")
public class KpiParameterConfigListRSP {
    @ApiModelProperty("主键id")
    private Long id;

    @ApiModelProperty("绩效参数code")
    private String configCode;

    @ApiModelProperty("绩效参数desc")
    private String configDesc;
}
