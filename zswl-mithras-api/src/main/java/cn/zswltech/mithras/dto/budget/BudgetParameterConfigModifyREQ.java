package cn.zswltech.mithras.dto.budget;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
/**
 * @description 预算管理-参数设置
 * @author vico
 * @date 2025-04-11
 */
@Data
@ApiModel("预算管理-参数设置编辑-请求体")
public class BudgetParameterConfigModifyREQ {

    /**
    * 主键id
    */
    @ApiModelProperty(value = "主键id")
    private Long id;

    /**
    * 参数value（json）
    */
    @ApiModelProperty(value = "参数value（json）")
    private String configValue;

}
