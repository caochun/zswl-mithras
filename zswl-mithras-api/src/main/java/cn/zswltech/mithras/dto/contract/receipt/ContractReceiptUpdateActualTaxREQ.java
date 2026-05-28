package cn.zswltech.mithras.dto.contract.receipt;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @description:
 * @author: zhangxin
 * @date: 2024/1/16
 */
@Data
@ApiModel("更新税额和不含税租金-请求体")
public class ContractReceiptUpdateActualTaxREQ {

    @ApiModelProperty("借据id")
    @NotNull
    private Long receiptId;

    @ApiModelProperty("不含税租金（元）")
    private Long rentExcludingTax;

    @ApiModelProperty("税额（元）")
    private Long tax;
}
