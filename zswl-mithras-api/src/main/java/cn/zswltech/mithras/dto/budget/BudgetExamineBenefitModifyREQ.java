package cn.zswltech.mithras.dto.budget;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
/**
 * @description 预算管理-预算考核-效益考核表
 * @author vico
 * @date 2025-04-11
 */
@Data
@ApiModel("预算管理-预算考核-效益考核表编辑-请求体")
public class BudgetExamineBenefitModifyREQ {

    /**
    * 主键id
    */
    @ApiModelProperty(value = "主键id")
    private Long id;

    /**
    * 字段值
    */
    @ApiModelProperty(value = "字段值")
    private Long fieldValue;

}
