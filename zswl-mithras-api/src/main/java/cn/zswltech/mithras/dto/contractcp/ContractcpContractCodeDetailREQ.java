package cn.zswltech.mithras.dto.contractcp;

import cn.zswltech.mithras.dto.PageReq;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import javax.validation.constraints.NotNull;
/**
 * @create: 2022-08-15
 **/

@Data
@ApiModel("合同收付款详情-请求体")
public class ContractcpContractCodeDetailREQ{

    @NotNull
    @ApiModelProperty("项目编号")
    private String projCode;


}
