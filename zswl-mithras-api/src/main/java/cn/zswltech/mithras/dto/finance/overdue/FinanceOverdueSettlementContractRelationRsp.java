package cn.zswltech.mithras.dto.finance.overdue;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @description 应收逾期结算表
 * @author vico
 * @date 2025-09-15
 */
@Data
@ApiModel("逾期查询客户下合同信息-返回体")
public class FinanceOverdueSettlementContractRelationRsp {

    @ApiModelProperty("合同ID")
    private Long contractId;

    @ApiModelProperty("合同编号")
    private String contractCode;

    private List<SettlementContractRelationFlowItem> flowItem;

    @Data
    public static class SettlementContractRelationFlowItem{
        @ApiModelProperty("合同ID")
        private Long flowId;

        @ApiModelProperty("合同编号")
        private String flowCode;
    }

}
