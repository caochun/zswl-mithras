package cn.zswltech.mithras.dto.contract.settle;

import cn.zswltech.mithras.dto.ListBaseRSP;
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
@ApiModel("合同结清方案数据-返回体")
public class ContractSettlePlanDetailRSP extends ListBaseRSP {
    @ApiModelProperty("结清方案id")
    private Long id;

    @ApiModelProperty("主合同id")
    private Long contractId;

    @ApiModelProperty("合同结清方案类型 SETTLE_NORMAL-正常结清 SETTLE_IN_ADVANCE-提前结清")
    private String planType;

    @ApiModelProperty("到期未付租金")
    private Long outstandingRent;

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

    @ApiModelProperty("保证金是否内扣 0-否 1-是")
    private Integer isEarnestDeduction;

    @ApiModelProperty("名义货价")
    private Long nominalPrice;

    @ApiModelProperty("申请减免金额")
    private Long applyDerateAmount;

    @ApiModelProperty("原到期日")
    private String originalDeadline;

    @ApiModelProperty("合计金额")
    private Long totalAmount;

    @ApiModelProperty("申请结清日期")
    private String applySettleDate;

    @ApiModelProperty("结清说明")
    private String settleRemark;
}
