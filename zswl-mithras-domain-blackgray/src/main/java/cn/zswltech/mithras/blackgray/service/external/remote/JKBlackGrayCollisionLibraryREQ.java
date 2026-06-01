package cn.zswltech.mithras.blackgray.service.external.remote;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @description 黑灰名单撞库
 * @author
 * @date 2023-11-28
 */
@Data
@ApiModel("黑灰名单撞库-请求体")
public class JKBlackGrayCollisionLibraryREQ {

    /**
     * 企业名称
     */
    @ApiModelProperty(value = "企业名称")
    @NotNull(message = "企业名称不能为空")
    private String enterpriseName;

    /**
     * 统一社会信用代码
     */
    @ApiModelProperty(value = "统一社会信用代码")
    @NotNull(message = "统一社会信用代码不能为空")
    private String unifiedSocialCreditCode;

    /**
     * 业务类型
     */
    @ApiModelProperty(value = "业务类型")
    @NotNull(message = "业务类型不能为空")
    private String businessType;

    private String orgCode;

}
