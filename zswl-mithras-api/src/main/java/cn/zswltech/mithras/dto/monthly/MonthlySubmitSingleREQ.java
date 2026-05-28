package cn.zswltech.mithras.dto.monthly;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author yangxiong
 * @date 2024/8/6/09:19
 * @description
 */
@Data
public class MonthlySubmitSingleREQ {

    @ApiModelProperty(value = "记录ID")
    @NotNull(message = "记录ID不得为空")
    private Long id;

    @ApiModelProperty(value = "主数据ID")
    @NotNull(message = "主数据ID不得为空")
    private Long mainId;

    /**
     * tabType {@link MonthlyModuleTypeEnum}
     */
    @ApiModelProperty(value = "tabType")
    @NotBlank(message = "tabType不得为空")
    private String tabType;
}
