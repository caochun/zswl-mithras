package cn.zswltech.mithras.dto.contract.receipt;

import cn.zswltech.mithras.dto.VersionBaseREQ;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

/**
 * @description:
 * @author: zhangxin
 * @date: 2024/1/16
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel("查询税额和不含税租金-请求体")
public class ContractReceiptQueryActualTaxREQ extends VersionBaseREQ {

    @NotNull(message = "合同id不能为空")
    @ApiModelProperty("主合同id")
    private Long contractId;
}
