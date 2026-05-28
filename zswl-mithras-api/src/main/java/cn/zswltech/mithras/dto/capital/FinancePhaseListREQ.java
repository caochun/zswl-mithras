package cn.zswltech.mithras.dto.capital;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author yangxiong
 * @date 2024/5/19/11:23
 * @description
 */
@Data
public class FinancePhaseListREQ {

    @ApiModelProperty(value = "融资idKey")
    @NotBlank(message = "idKey不能为空")
    private String idKey;

    @ApiModelProperty(value = "现金流项目")
    @NotNull(message = "现金流项目不能为空")
    private String cashFlowItem;

}
