package cn.zswltech.mithras.dto.app;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author yibin
 */
@Data
public class AppContractSignDetailREQ {


    @ApiModelProperty("合同id")
    private String contractId;

}
