package cn.zswltech.mithras.dto.contract;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author bigbear
 * @date 2025/3/21 14:52
 * @description
 */
@Data
@ApiModel(value = "合同评级机构列表-请求参数")
public class ContractEvaluationAgencyListREQ {

    @ApiModelProperty(value = "合同id")
    @NotNull(message = "合同id不能为空")
    private Long contractId;
}
