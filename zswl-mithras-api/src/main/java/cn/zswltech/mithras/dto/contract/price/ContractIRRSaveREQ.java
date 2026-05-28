package cn.zswltech.mithras.dto.contract.price;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author dingqi
 * @date 2023/1/11
 * @description
 */
@Data
@ApiModel("合同-保存IRR-请求体")
public class ContractIRRSaveREQ {
    @ApiModelProperty("合同id")
    @NotNull(message = "合同id不能为空")
    private Long contractId;

    @ApiModelProperty("irr")
    @NotNull(message = "irr不能为空")
    private Integer irrPercent;
}
