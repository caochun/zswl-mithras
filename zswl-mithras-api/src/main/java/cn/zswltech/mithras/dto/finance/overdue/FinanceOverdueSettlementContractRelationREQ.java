package cn.zswltech.mithras.dto.finance.overdue;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @description 应收逾期结算表
 * @author vico
 * @date 2025-09-15
 */
@Data
@ApiModel("应收逾期结算表新增-请求体")
public class FinanceOverdueSettlementContractRelationREQ {

    /**
    * 收款明细id
    */
    @ApiModelProperty(value = "客户id")
    private Long clientId;

}
