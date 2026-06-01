package cn.zswltech.mithras.blackgray.dto.rsp;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @description 黑灰名单撞库
 * @author
 * @date 2023-11-28
 */
@Data
@ApiModel("黑灰名单撞库-返回体")
public class BlackGrayCollisionLibraryRSP {

    /**
     * 企业名称
     */
    @ApiModelProperty(value = "企业名称")
    private String enterpriseName;


    @ApiModelProperty(value = "状态代码")
    private String enterpriseStatusCode;

    @ApiModelProperty(value = "名称")
    private String enterpriseStatusName;

}
