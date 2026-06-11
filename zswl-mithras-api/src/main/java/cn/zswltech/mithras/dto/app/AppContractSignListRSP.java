package cn.zswltech.mithras.dto.app;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


/**
 * @author junke
 */
@ApiModel("融租易合同信息-返回体")
@Data
public class AppContractSignListRSP {

    @ApiModelProperty(value = "合同id")
    private Long id;
    /**
     * 承租人类型
     */
    @ApiModelProperty(value = "承租人类型")
    private String lesseeType;
    /**
     * 承租人名称
     */
    @ApiModelProperty(value = "承租人名称")
    private String lesseeName;

    @ApiModelProperty(value = "合同编号")
    private String contractCode;

    @ApiModelProperty(value = "合同金额")
    private Long applyCreditAmount;

}
