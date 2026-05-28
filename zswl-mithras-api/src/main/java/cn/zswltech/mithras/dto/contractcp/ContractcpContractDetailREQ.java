package cn.zswltech.mithras.dto.contractcp;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @create: 2022-08-15
 **/

@Data
@ApiModel("合同详情-请求体")
public class ContractcpContractDetailREQ{

    @NotNull
    @ApiModelProperty("合同Id")
    private Long contractId;



}
