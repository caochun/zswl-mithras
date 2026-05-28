package cn.zswltech.mithras.dto.contract.baseinfo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author dingqi
 * @date 2023/2/2
 * @description
 */
@Data
@ApiModel("检查是否可发起合同变更-请求体")
public class ContractCanDoChangeCheckREQ {
    @ApiModelProperty("合同id")
    @NotNull(message = "合同id不能为空")
    private Long contractId;

    @ApiModelProperty("变更类型")
    @NotBlank(message = "变更类型不能为空")
    private String changeType;
}
