package cn.zswltech.mithras.dto.ftp;

import cn.zswltech.mithras.dto.VersionBaseREQ;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2023/1/9 17:07
 */
@Data
@ApiModel("季度指导 id-请求体")
public class FtpGuidanceIdReq extends VersionBaseREQ {
    @NotNull(message = "id必填")
    @ApiModelProperty("指导id")
    private Long id;
}
