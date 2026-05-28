package cn.zswltech.mithras.dto.filingmaterials;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author lllin
 * @date 2022/8/15
 */
@Data
public class FilingBasicRemoveREQ {
    @NotNull(message = "业务类型为空")
    @ApiModelProperty("业务类型")
    private String moduleType;
    @ApiModelProperty("文件id")
    @NotNull(message = "文件id为空")
    private Long fileId;
    @ApiModelProperty("申请id")
    @NotNull(message = "申请id为空")
    private Long mainId;
    @ApiModelProperty("客户id")
    private Long sourceBusinessKey;

}
