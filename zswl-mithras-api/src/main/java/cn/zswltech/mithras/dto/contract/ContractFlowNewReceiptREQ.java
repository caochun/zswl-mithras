package cn.zswltech.mithras.dto.contract;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author dingqi
 * @date 2022/8/24
 * @description
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel("合同管理-新增借据（提交审批）-请求体")
public class ContractFlowNewReceiptREQ extends ContractFlowBasicREQ {
    @ApiModelProperty("借据id")
    private Long receiptId;
}
