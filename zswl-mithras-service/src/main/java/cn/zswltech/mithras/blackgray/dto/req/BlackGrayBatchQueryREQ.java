package cn.zswltech.mithras.blackgray.dto.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @description 黑灰名单库
 * @author
 * @date 2023-11-28
 */
@Data
@ApiModel("黑灰名风险规模-请求体")
public class BlackGrayBatchQueryREQ {
    @ApiModelProperty(value = "企业名称")
    @NotBlank(message = "企业名称不能为空")
    private String enterpriseName;

    @ApiModelProperty(value = "统一社会信用代码")
    @NotBlank(message = "统一社会信用代码不能为空")
    private String unifiedSocialCreditCode;

    @ApiModelProperty(value = "业务类型")
    @NotBlank(message = "业务类型不能为空")
    private String businessType;

}
