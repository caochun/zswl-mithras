package cn.zswltech.mithras.dto.capital;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author yangxiong
 * @date 2024/5/19/16:39
 * @description
 */
@Data
public class FinanceFlowDeleteREQ {

    @NotNull(message = "流水的ID列表不能为空")
    @ApiModelProperty(value = "流水的ID列表")
    private List<Long> financeFlowIds;
}
