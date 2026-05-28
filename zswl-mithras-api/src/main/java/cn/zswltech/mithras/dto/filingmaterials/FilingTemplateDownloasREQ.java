package cn.zswltech.mithras.dto.filingmaterials;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author jackerhe
 * @date 2022/8/15
 */
@Data
public class FilingTemplateDownloasREQ {
    @NotNull(message = "业务类型为空")
    @ApiModelProperty("业务类型")
    private String businessType;
    @ApiModelProperty("申请id")
    @NotNull(message = "申请id为空")
    private Long id;
    @ApiModelProperty("客户id")
    private Long clientId;

}
