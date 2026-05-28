package cn.zswltech.mithras.dto.app;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author junke
 */
@ApiModel("融租易合同信息-返回体")
@Data
public class AppContractSignProjExistedRSP {

    @ApiModelProperty(value = "合同编号")
    private String contractCode;

    @ApiModelProperty(value = "合同id")
    private Long contractId;
}
