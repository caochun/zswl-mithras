package cn.zswltech.mithras.dto.capital;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;

/**
 * @author yangxiong
 * @date 2024/5/18/14:49
 * @description
 */
@Data
public class BankFlowContractReceiptListRSP {

    @ApiModelProperty(value = "主键id")
    private Long id;

    @ApiModelProperty(value = "合同id")
    private Long contractId;

    @ApiModelProperty(value = "借据编号")
    private String receiptCode;

    @ApiModelProperty(value = "借据id")
    private Long receiptId;

    @ApiModelProperty(value = "实付金额")
    private Long paidInAmount;

    @ApiModelProperty(value = "实付日期")
    private LocalDate paidInDate;

}
