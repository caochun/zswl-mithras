package cn.zswltech.mithras.dto.contract.baseinfo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author dingqi
 * @date 2023/1/3
 * @description
 */
@Data
@ApiModel("合同-变更说明-请求体")
public class ContractChangeRemarkREQ {
    @NotNull(message = "合同id不能为空")
    @ApiModelProperty("合同id")
    private Long contractId;

    @NotBlank(message = "变更说明不能为空")
    @ApiModelProperty("变更说明")
    private String changeRemark;
}
