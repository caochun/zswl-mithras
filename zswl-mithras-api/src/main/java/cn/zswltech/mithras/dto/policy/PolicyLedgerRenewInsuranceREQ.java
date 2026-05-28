package cn.zswltech.mithras.dto.policy;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


@Data
@ApiModel("保单台账-请求体")
public class PolicyLedgerRenewInsuranceREQ {

    //@NotNull
    @ApiModelProperty("保单id")
    private Long id;

}
