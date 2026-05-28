package cn.zswltech.mithras.dto.monthly;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@ApiModel("实际利率法-列表-参数")
public class MonthlyExcelREQ {

    @NotBlank(message = "月份不得为空")
    @ApiModelProperty(value = "处理月份 yyyy-MM")
    private String yearAndMonth;


    @NotBlank(message = "模块类型不得为空")
    @ApiModelProperty(value = "模块类型 MonthlyModuleTypeEnum")
    private String moduleType;
}
