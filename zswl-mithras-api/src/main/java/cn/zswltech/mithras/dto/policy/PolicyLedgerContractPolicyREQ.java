package cn.zswltech.mithras.dto.policy;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;


@Data
@ApiModel("保单台账-合同保单请求体")
public class PolicyLedgerContractPolicyREQ {

    @NotNull(message = "合同id不能为空")
    @ApiModelProperty("contractId")
    private Long contractId;

    @ApiModelProperty("policyId")
    private List<Long> policyIds;
}
