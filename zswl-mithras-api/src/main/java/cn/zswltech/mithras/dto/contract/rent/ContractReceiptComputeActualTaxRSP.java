package cn.zswltech.mithras.dto.contract.rent;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @description:
 * @author: zhangxin
 * @date: 2024/1/16
 */
@Data
@Builder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@ApiModel("查询税额和不含税租金-返回体")
public class ContractReceiptComputeActualTaxRSP {

    @ApiModelProperty("借据id")
    private Long receiptId;

    @ApiModelProperty("税率(%)")
    private Long taxRate;

    @ApiModelProperty("不含税利息(元)")
    private Long excludingInterestTax;

    @ApiModelProperty("不含税租金(元)")
    private Long rentExcludingTax;

    @ApiModelProperty("税额(元)")
    private Long tax;

    @ApiModelProperty("印花税")
    private Long stampDuty;
}
