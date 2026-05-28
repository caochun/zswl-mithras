package cn.zswltech.mithras.dto.contract;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author dingqi
 * @date 2023/4/3
 * @description
 */
@ApiModel("合同操作准备-请求体")
@Data
public class ContractOperationPrepareREQ {
    @ApiModelProperty("合同id")
    private Long contractId;

    @NotBlank(message = "合同操作不能为空")
    @ApiModelProperty("合同操作，详见枚举contractOperationEnum")
    private String operation;

    @ApiModelProperty("合同id")
    private Long commitPrepareId;
}
