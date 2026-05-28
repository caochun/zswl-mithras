package cn.zswltech.mithras.dto.ftp;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2023/1/11 14:58
 */
@ApiModel("版本差异比较-入参")
@Data
public class FtpVersionDiffREQ {
    @ApiModelProperty("版本id")
    @NotNull
    private Long id;
}
