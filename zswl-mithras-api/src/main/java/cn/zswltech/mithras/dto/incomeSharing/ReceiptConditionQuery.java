package cn.zswltech.mithras.dto.incomeSharing;

import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.NotBlank;


@Data
public class ReceiptConditionQuery {

    @ApiModelProperty(value = "客户名称")
    private Long clientId;

    @ApiModelProperty(value = "项目名称")
    private String projName;

    @ApiModelProperty(value = "合同编号")
    private String contractCode;

    @ApiModelProperty(value = "借据编号")
    private String receiptCode;

    @ApiModelProperty(value = "合同状态")
    private String contractStatus;

    @ApiModelProperty(value = "是否逾期:OVERDUE(逾期)，NOT_OVERDUE(未逾期)")
    private String overdueType;
}
