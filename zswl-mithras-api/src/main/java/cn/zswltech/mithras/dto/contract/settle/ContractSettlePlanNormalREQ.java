package cn.zswltech.mithras.dto.contract.settle;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author dingqi
 * @date 2022/8/24
 * @description
 */
@Data
@ApiModel("合同正常结清（保存）-请求体")
public class ContractSettlePlanNormalREQ {
    @ApiModelProperty("结清方案id")
    private Long id;

    @NotNull(message = "主合同id不能为空")
    @ApiModelProperty("主合同id")
    private Long contractId;

    @NotNull(message = "到期未付租金不能为空")
    @ApiModelProperty("到期未付租金")
    private Long outstandingRent;

    @NotNull(message = "未到期本金不能为空")
    @ApiModelProperty("未到期本金")
    private Long beforeMaturityPrincipal;

    @ApiModelProperty("未到期利息")
    private Long beforeMaturityInterest;

    @ApiModelProperty("损失金")
    private Long loss;

    @ApiModelProperty("违约金")
    private Long liquidatedDamages;

    @ApiModelProperty("保证金余额")
    private Long earnestBalance;

    //@NotNull(message = "保证金是否内扣不能为空")
    @ApiModelProperty("保证金是否内扣 0-否 1-是")
    private Integer isEarnestDeduction;

    @NotNull(message = "名义价款不能为空")
    @ApiModelProperty("名义货价")
    private Long nominalPrice;

    @ApiModelProperty("申请减免金额")
    private Long applyDerateAmount;

    @NotBlank(message = "原到期日不能为空")
    @ApiModelProperty("原到期日")
    private String originalDeadline;

//    @ApiModelProperty("合计金额")
//    private Long totalAmount;
}
