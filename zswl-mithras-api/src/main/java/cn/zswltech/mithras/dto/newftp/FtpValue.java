package cn.zswltech.mithras.dto.newftp;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2023/5/22 17:49
 */
@Data
@Accessors(chain = true)
public class FtpValue {
    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "ftp值")
    private Integer value;
}
