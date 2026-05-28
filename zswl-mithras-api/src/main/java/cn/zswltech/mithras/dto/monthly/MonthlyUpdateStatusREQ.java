package cn.zswltech.mithras.dto.monthly;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author bigbear
 */
@Data
public class MonthlyUpdateStatusREQ {

    @NotNull(message = "主数据ID不能为空")
    @ApiModelProperty(value = "主数据ID")
    private Long mainId;

    @NotNull(message = "被修改记录ID不能为空")
    @ApiModelProperty(value = "被修改记录ID")
    private Long recordId;

    @NotNull(message = "状态不能为空")
    @ApiModelProperty(value = "状态")
    private Integer status;

    @NotBlank(message = "TAB类型枚举不能为空")
    @ApiModelProperty(value = "TAB类型枚举 MonthlyModuleTypeEnum")
    private String tabType;
}
