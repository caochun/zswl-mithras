package cn.zswltech.mithras.dto.leaseholdproperty;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;


@Data
@ApiModel("租赁物审核-变更流程提交-请求")
public class LeaseReviewEffectREQ {

    @ApiModelProperty("合同id")
    @NotNull(message = "合同id不能为空")
    private Long contractId;

    @ApiModelProperty(value = "存量合同标识，0否 1是")
    private Integer stockContractFlag;

    /*@ApiModelProperty("合同流程ID")
    @NotNull(message = "合同流程ID不能为空")
    private String flowId;*/

}
