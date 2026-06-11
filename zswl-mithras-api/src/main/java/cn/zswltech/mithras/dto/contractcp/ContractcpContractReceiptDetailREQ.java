package cn.zswltech.mithras.dto.contractcp;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


/**
 * @create: 2022-08-15
 **/

@Data
@ApiModel("合同详情-请求体")
public class ContractcpContractReceiptDetailREQ {


    @ApiModelProperty("合同Id")
    private Long contractId;

    @ApiModelProperty("借据编号")
    private String receiptCode;

}
