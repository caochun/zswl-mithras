package cn.zswltech.mithras.dto.file;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * @author yangxiong
 * @date 2024/3/18/19:16
 * @description
 */
@Data
public class FileAuthenticationConfigREQ implements Serializable {
    private static final long serialVersionUID = -3247659578688696005L;

    @ApiModelProperty(value = "岗位")
    private String post;

    @ApiModelProperty(value = "文件模版名称")
    private String fileName;

    @ApiModelProperty(value = "用户ID")
    private Long userId;

    @ApiModelProperty(value = "文件类型")
    @NotBlank(message = "文件类型不能为空！")
    private String fileType;
}
