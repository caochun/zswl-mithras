package cn.zswltech.mithras.dto.kpi.parameterconfig;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @author dingqi
 * @date 2023/2/10
 * @description
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel("税率维护")
public class TaxRateConfig extends KpiParameterConfigBase {
    @ApiModelProperty("参数值")
    private List<Data> configValue;

    @EqualsAndHashCode(callSuper = true)
    @lombok.Data
    public static class Data extends DataBase {
        @ApiModelProperty("税种")
        private String taxType;

        @ApiModelProperty("业务类型")
        private String bizType;

        @ApiModelProperty("税率")
        private String taxRate;
    }
}
