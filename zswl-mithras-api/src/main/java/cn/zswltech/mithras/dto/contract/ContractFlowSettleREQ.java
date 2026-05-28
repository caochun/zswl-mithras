package cn.zswltech.mithras.dto.contract;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

/**
 *
 *
 * @author wangchuanhao
 * @date 2022/8/23 11:02 AM
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel("合同管理-合同结清提交审批-基础请求体")
public class ContractFlowSettleREQ extends ContractFlowBasicREQ {

    @ApiModelProperty("结清类型 NORMAL-正常结清 IN_ADVANCE-提前结清")
    @NotNull
    private String settlePlanType;

    @NotNull(message = "请先保存结清方案")
    @ApiModelProperty("结清方案id")
    private Long settlePlanId;

}
