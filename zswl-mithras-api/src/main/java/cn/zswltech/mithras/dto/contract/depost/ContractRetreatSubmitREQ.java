package cn.zswltech.mithras.dto.contract.depost;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("保证金退抵提交审批-请求体")
public class ContractRetreatSubmitREQ {

    @ApiModelProperty("主键id")
    private Long id;

    @ApiModelProperty("保证金余额")
    private Long collectionAmount;

    @ApiModelProperty("关联合同id")
    private String contractId;

    @ApiModelProperty("合同编号")
    private String contractCode;

    @ApiModelProperty("客户名称")
    private String clientName;

    @ApiModelProperty("流程状态")
    private String processStatus;

    @ApiModelProperty("保证金内扣金额")
    private Long deductionAmount;

    @ApiModelProperty("保证金退还金额")
    private Long returnedAmount;

    @ApiModelProperty("是否回收保证金")
    private String recyclingFlag;
}
