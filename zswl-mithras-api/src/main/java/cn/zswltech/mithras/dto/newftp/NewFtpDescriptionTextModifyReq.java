package cn.zswltech.mithras.dto.newftp;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2023/5/26 11:32
 */
@Data
@ApiModel("FTP描述文本更新-请求体")
public class NewFtpDescriptionTextModifyReq {
    @ApiModelProperty(value = "id")
    @NotNull
    private Long id;

    @ApiModelProperty(value = "描述类型")
    private String descContent;
}
