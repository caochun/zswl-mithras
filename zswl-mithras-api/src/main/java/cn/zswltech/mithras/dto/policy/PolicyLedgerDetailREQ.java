package cn.zswltech.mithras.dto.policy;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;


@Data
@ApiModel("保单台账-请求体")
public class PolicyLedgerDetailREQ {

    //@NotNull
    @ApiModelProperty("id")
    private Long id;

    private Long contractId;

    @ApiModelProperty("数据来源")
    @NotNull
    private String dataSource;

}
