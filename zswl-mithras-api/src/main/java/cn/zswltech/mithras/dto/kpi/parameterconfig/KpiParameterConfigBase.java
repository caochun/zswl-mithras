package cn.zswltech.mithras.dto.kpi.parameterconfig;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author dingqi
 * @date 2023/2/10
 * @description
 */
@Data
public abstract class KpiParameterConfigBase {
    @ApiModelProperty("主键id")
    private Long id;

    @ApiModelProperty("参数类型code")
    private String configCode;

    @ApiModelProperty("参数类型描述")
    private String configDesc;

    @Data
    public static abstract class DataBase {
        @ApiModelProperty("参数值类型 VALUE-纯值 FORMULA-公式")
        private String configValueType;
    }
}
