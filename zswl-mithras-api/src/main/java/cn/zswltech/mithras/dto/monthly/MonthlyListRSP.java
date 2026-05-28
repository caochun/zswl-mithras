package cn.zswltech.mithras.dto.monthly;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDate;

@Data
@Accessors(chain = true)
@ApiModel("列表-参数")
public class MonthlyListRSP{

    @ApiModelProperty("主ID")
    private Long id;

    @ApiModelProperty("月份")
    private String yearAndMonth;

    @ApiModelProperty("实际利率法(含税)")
    private Long airCount;

    @ApiModelProperty("实际利率法(不含税)")
    private Long airCountExcludeTax;

    @ApiModelProperty("剩余本金法(含税)")
    private Long rpCount;

    @ApiModelProperty("剩余本金法(不含税)")
    private Long rpCountExcludeTax;

    @ApiModelProperty("当期计提成本(含税)")
    private Long costCount;

    @ApiModelProperty("当期计提成本(不含税)")
    private Long costCountExcludeTax;

    @ApiModelProperty("印花税")
    private Long stampDutyCount;

    @ApiModelProperty("确认日期")
    private LocalDate confirmDate;

    @ApiModelProperty("状态 MonthlyManagementStatusEnum")
    private String status;

    @ApiModelProperty("关账日期")
    private String closeDate;
}
