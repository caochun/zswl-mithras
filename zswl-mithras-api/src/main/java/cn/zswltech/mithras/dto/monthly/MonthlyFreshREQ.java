package cn.zswltech.mithras.dto.monthly;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;

/**
 * @author bigbear
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@ApiModel("实际利率法-刷新-参数")
public class MonthlyFreshREQ {

    @NotBlank(message = "月份不得为空")
    @ApiModelProperty(value = "处理月份 yyyy-MM")
    private String yearAndMonth;

//    @ApiModelProperty(value = "刷新类型")
//    @NotBlank(message = "刷新类型不得为空")
//    private String refreshType;

    private Long sourceId;

    /**
     * {@link cn.zswltech.mithras.finance.monthly.enums.MonthlyModuleTypeEnum}
     */
    @ApiModelProperty(value = "TabType")
    private String tabType ;
}
