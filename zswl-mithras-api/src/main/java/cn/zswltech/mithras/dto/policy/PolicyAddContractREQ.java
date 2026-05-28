package cn.zswltech.mithras.dto.policy;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;


@Data
@ApiModel("新增保单合同-请求体")
public class PolicyAddContractREQ {

    @NotNull
    @ApiModelProperty("projId")
    private Long projId;

}
