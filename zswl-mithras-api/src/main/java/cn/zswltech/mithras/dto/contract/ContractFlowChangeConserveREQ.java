package cn.zswltech.mithras.dto.contract;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 *
 * lpr调整， 展期， 调整还款计划
 * @author
 * @date 2022/8/23 11:02 AM
 */
@Data
@ApiModel("合同变更-合同保存-基础请求体")
public class ContractFlowChangeConserveREQ {

    @ApiModelProperty("变更类型;CHANGE_INTEREST（调息）、EARLY_REPAYMENT（提前还款）、EXTENSION（展期）、OTHER（其他）")
    @NotBlank
    private String changeType;

    @ApiModelProperty(value = "合同id")
    @NotNull
    private Long contractId;

    /**
     * lpr
     */
    @ApiModelProperty(value = "LPR调整-lpr")
    private Integer lprPercent;


    /*@ApiModelProperty(value = "展期-展期月数")
    private int extensionMonth;*/

    @ApiModelProperty(value = "展期-租赁期限月数")
    private Integer leaseMonthCount;


}
