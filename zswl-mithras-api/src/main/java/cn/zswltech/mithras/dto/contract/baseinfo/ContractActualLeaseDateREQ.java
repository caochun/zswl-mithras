package cn.zswltech.mithras.dto.contract.baseinfo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author dingqi
 * @date 2022/9/6
 * @description
 */
@Data
@ApiModel("更新合同实际起租日-请求体")
public class ContractActualLeaseDateREQ {
    @NotNull(message = "主合同id不能为空")
    @ApiModelProperty("主合同id")
    private Long id;

    @NotBlank(message = "实际起租日不能为空")
    @ApiModelProperty("实际起租日 (yyyy-MM-dd)")
    private String actualLeaseDate;
}
