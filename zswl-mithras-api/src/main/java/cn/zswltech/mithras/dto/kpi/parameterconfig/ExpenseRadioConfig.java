package cn.zswltech.mithras.dto.kpi.parameterconfig;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @author dingqi
 * @date 2023/2/13
 * @description
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel("费用计提比例")
public class ExpenseRadioConfig extends KpiParameterConfigBase {
    @ApiModelProperty("参数值")
    private List<ExpenseRadioConfig.Data> configValue;

    @EqualsAndHashCode(callSuper = true)
    @lombok.Data
    public static class Data extends DataBase {
        @ApiModelProperty("考核部门")
        private String assessDept;
        @ApiModelProperty("费用计提比例")
        private String expenseRadio;
    }
}
