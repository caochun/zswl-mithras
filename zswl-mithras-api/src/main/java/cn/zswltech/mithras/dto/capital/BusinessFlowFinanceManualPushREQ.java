package cn.zswltech.mithras.dto.capital;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * @author dingqi
 * @date 2024/7/10
 * @description
 */
@Data
public class BusinessFlowFinanceManualPushREQ {
    @ApiModelProperty("业务现金流编号")
    @NotEmpty(message = "业务现金流编号不能为空")
    private List<String> cashFlowCodeList;
}
