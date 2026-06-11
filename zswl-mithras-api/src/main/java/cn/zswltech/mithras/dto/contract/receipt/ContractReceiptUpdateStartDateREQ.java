package cn.zswltech.mithras.dto.contract.receipt;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author yangxiong
 * @date 2024/8/26/17:15
 * @description
 */
@Data
public class ContractReceiptUpdateStartDateREQ {

    @ApiModelProperty("借据id")
    @NotNull(message = "借据id不能为空")
    private Long receiptId;

    @ApiModelProperty("起租日期 format: yyyy-MM-dd")
    @NotNull(message = "起租日期不能为空")
    private String receiptStartDate;

    @ApiModelProperty("流程实例ID-仅在流程中需要")
    private String processInstanceId;
}
