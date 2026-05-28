package cn.zswltech.mithras.dto.budget;
import lombok.Data;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotNull;
/**
 * @description 预算管理-预算考核-预算执行情况表
 * @author vico
 * @date 2025-04-11
 */
@Data
@ApiModel("预算管理-预算考核-预算执行情况表删除-请求体")
public class BudgetExamineBudgetExecuteRemoveREQ {

    @NotNull
    @ApiModelProperty("id")
    private Long id;

}
