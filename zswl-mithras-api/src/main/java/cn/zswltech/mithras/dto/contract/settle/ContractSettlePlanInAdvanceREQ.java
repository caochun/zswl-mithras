package cn.zswltech.mithras.dto.contract.settle;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;

/**
 * @author dingqi
 * @date 2022/8/24
 * @description
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel("合同提前结清（保存）-请求体")
public class ContractSettlePlanInAdvanceREQ extends ContractSettlePlanNormalREQ {
    @NotBlank(message = "申请结清日期不能为空")
    @ApiModelProperty("申请结清日期")
    private String applySettleDate;

    @NotBlank(message = "提前结清说明不能为空")
    @ApiModelProperty("结清说明")
    private String settleRemark;
}
