package cn.zswltech.mithras.dto.kpi.parameterconfig;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @author dingqi
 * @date 2023/2/14
 * @description
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ProfitAdjustConfig extends KpiParameterConfigBase {
    @ApiModelProperty("参数值")
    private List<Data> configValue;

    @EqualsAndHashCode(callSuper = true)
    @lombok.Data
    public static class Data extends DataBase {
        @ApiModelProperty("区间分组")
        private String group;

        @ApiModelProperty("利润完成率范围code")
        private String rangeCode;

        @ApiModelProperty("系数")
        private String coefficient;
    }
}
