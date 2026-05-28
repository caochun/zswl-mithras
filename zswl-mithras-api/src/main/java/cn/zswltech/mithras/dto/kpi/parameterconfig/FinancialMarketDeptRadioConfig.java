package cn.zswltech.mithras.dto.kpi.parameterconfig;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author dingqi
 * @date 2023/2/14
 * @description
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class FinancialMarketDeptRadioConfig extends KpiParameterConfigBase {
    private Data configValue;

    @EqualsAndHashCode(callSuper = true)
    @lombok.Data
    public static class Data extends DataBase {
        @ApiModelProperty("金融市场部提奖比例")
        private String financialMarketDeptRadio;
    }
}
