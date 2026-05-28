package cn.zswltech.mithras.dto.materialsfile;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author dingqi
 * @date 2022/8/18
 * @description
 */
@Data
@ApiModel("合同资料清单-请求体")
public class ContractMaterialListREQ {
    @NotNull(message = "主合同id不能为空")
    @ApiModelProperty("主合同id")
    private Long contractId;
}
