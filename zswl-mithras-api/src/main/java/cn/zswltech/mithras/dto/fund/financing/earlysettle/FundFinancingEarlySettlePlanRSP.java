package cn.zswltech.mithras.dto.fund.financing.earlysettle;

import cn.zswltech.mithras.dto.ListBaseRSP;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @author dingqi
 * @date 2023/2/23
 * @description
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel("融资管理-提前结清-提前结清方案-返回体")
public class FundFinancingEarlySettlePlanRSP extends ListBaseRSP {
    @ApiModelProperty("id")
    private Long id;

    @ApiModelProperty("融资id")
    private Long financingId;

    @ApiModelProperty("融资机构id")
    private List<Long> organizationId;

    @ApiModelProperty("融资机构名称")
    private List<String> organizationName;

    @ApiModelProperty("融资金额")
    private Long financingAmount;

    @ApiModelProperty("剩余本金")
    private Long lastPrincipal;

    @ApiModelProperty("融资期限")
    private String financingTerm;

    @ApiModelProperty("提前结清原因")
    private String reason;

    @ApiModelProperty("提前偿还金额")
    private Long earlyRepayAmount;

    @ApiModelProperty("提前偿还本金金额")
    private Long earlyPrincipleAmount;

    @ApiModelProperty("提前偿还利息金额")
    private Long earlyInterestAmount;

    @ApiModelProperty("违约金金额")
    private Long liquidatedDamagesAmount;

    @ApiModelProperty("其他费用金额")
    private Long otherFeeAmount;

    @ApiModelProperty("违约全减免金额")
    private Long remissionAmount;
}
