package cn.zswltech.mithras.dto.contract.settle;

import cn.zswltech.mithras.dto.contract.ContractSingleIdREQ;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;

/**
 * @author dingqi
 * @date 2022/9/6
 * @description
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel("获取结清方案-返回体")
public class ContractSettlePlanDetailREQ extends ContractSingleIdREQ {
    @NotBlank(message = "结清方案类型不能为空")
    @ApiModelProperty("结清方案类型 SETTLE_NORMAL-正常结清 SETTLE_IN_ADVANCE-提前结清")
    private String planType;

    @ApiModelProperty("是否需要实时计算 0不需要，其他需求")
    private Integer needReal;
}
