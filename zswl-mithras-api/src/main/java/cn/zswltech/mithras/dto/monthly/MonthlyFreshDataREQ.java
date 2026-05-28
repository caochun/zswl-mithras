package cn.zswltech.mithras.dto.monthly;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author bigbear
 */
@Data
public class MonthlyFreshDataREQ {

    @NotNull(message = "主数据ID不能为空")
    @ApiModelProperty(value = "主数据ID")
    private Long mainId;
}
