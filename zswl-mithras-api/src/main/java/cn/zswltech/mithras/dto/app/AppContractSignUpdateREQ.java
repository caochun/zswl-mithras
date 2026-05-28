package cn.zswltech.mithras.dto.app;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author yibin
 */
@Data
public class AppContractSignUpdateREQ {


    @ApiModelProperty("合同id")
    private String contractId;

    @ApiModelProperty(value = "是否签约")
    private Integer isSigned;
}
