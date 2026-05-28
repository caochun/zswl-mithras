package cn.zswltech.mithras.dto.financialcloudmetric;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2023/8/21 14:10
 */
@ApiModel("合同明细")
@Data
public class ContractDetail {

    @ApiModelProperty("id")
    private Long contractId;

    @ApiModelProperty("合同编号")
    private String contractCode;

    @ApiModelProperty("客户id")
    private Long clientId;

    @ApiModelProperty("客户名称")
    private String clientName;

    @ApiModelProperty("金额")
    private Long amount;
}
