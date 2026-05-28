package cn.zswltech.mithras.dto.contract.baseinfo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author dingqi
 * @date 2024/2/21
 * @description
 */
@Data
public class ContractAdjustRemarkREQ {
    @NotNull(message = "合同id不能为空")
    @ApiModelProperty("合同id")
    private Long contractId;

    @NotBlank(message = "调整说明不能为空")
    @ApiModelProperty("调整说明")
    private String adjustRemark;
}
