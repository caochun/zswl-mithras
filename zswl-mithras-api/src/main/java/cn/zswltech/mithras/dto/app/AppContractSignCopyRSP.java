package cn.zswltech.mithras.dto.app;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author junke
 */
@ApiModel("融租易合同信息-返回体")
@Data
public class AppContractSignCopyRSP {

    @ApiModelProperty(value = "文件id")
    private Long fileId;

    @ApiModelProperty(value = "合同id")
    private Long contractId;

    @ApiModelProperty(value = "业务类型")
    private String businessType;

    @ApiModelProperty(value = "资料类型")
    private String materialsType;

    @ApiModelProperty(value = "资料子类型")
    private String materialSubType;
}
