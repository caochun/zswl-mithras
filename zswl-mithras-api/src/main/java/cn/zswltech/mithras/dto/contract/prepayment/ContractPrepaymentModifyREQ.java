package cn.zswltech.mithras.dto.contract.prepayment;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;


@EqualsAndHashCode(callSuper = true)
@Data
public class ContractPrepaymentModifyREQ extends ContractPrepaymentAddREQ {
    @ApiModelProperty("id")
    private Long id;

    @ApiModelProperty("变更类型;CHANGE_INTEREST（调息）、EARLY_REPAYMENT（提前还款）、EXTENSION（展期）、OTHER（其他）")
    private String changeType;
}