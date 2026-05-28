package cn.zswltech.mithras.dto.contract.rent;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author dingqi
 * @date 2023/1/5
 * @description
 */
@Data
@ApiModel("合同-生成概算现金流-请求体")
public class ContractRentEstimateGenerateREQ {
    @NotNull(message = "合同id不能为空")
    @ApiModelProperty("合同id")
    private Long id;

    @NotBlank(message = "计划起租日不能为空")
    @ApiModelProperty("计划起租日")
    private String planStartDate;

    @NotBlank(message = "导入场景不能为空")
    @ApiModelProperty("导入场景")
    private String scene;
}
