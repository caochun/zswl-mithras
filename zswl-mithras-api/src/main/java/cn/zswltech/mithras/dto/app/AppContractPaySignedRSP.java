package cn.zswltech.mithras.dto.app;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author junke
 */
@ApiModel("融租易合同信息-返回体")
@Data
public class AppContractPaySignedRSP {


    @ApiModelProperty(value = "是否签约")
    private Integer isSigned;
}
