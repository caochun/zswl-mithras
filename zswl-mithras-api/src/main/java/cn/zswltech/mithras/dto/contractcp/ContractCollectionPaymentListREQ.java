package cn.zswltech.mithras.dto.contractcp;

import cn.zswltech.mithras.dto.PageReq;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @create: 2022-08-15
 **/

@Data
@ApiModel("合同收付款列表-请求体")
public class ContractCollectionPaymentListREQ extends PageReq {

    @ApiModelProperty("客户id")
    private Long clientId;

    @ApiModelProperty("合同编号")
    private String contractCode;

    @ApiModelProperty("项目名称")
    private String projName;

    @ApiModelProperty("合同状态")
    private String contractStatus;

    @ApiModelProperty("金额-从-暂时不用")
    private Long amountFrom;

    @ApiModelProperty("金额-到-暂时不用")
    private Long amountTo;
}
