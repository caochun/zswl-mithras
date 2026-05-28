package cn.zswltech.mithras.blackgray.dto.rsp;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @description 黑灰名单库
 * @author
 * @date 2023-11-28
 */
@Data
@ApiModel("黑灰名单库按企业汇总列表-返回体")
public class BlackGrayLibraryREQ {

    private Long clientId;

    @ApiModelProperty(value = "企业名称")
    //@NotNull(message = "企业名称不能为空")
    private String enterpriseName;

    /**
     * 统一社会信用代码
     */
    @ApiModelProperty(value = "统一社会信用代码")
    //@NotNull(message = "统一社会信用代码不能为空")
    private String unifiedSocialCreditCode;

}
