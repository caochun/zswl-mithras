package cn.zswltech.mithras.dto.ftp;
import lombok.Data;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotNull;

/**
 * @description ftp_quarterly_guidance
 * @author zhaozhengkang
 * @date 2023-01-09
 */
@Data
@ApiModel("新增季度指导-请求体")
public class FtpQuarterlyGuidanceAddReq {
    @ApiModelProperty(value = "年度")
    @NotNull(message = "年度为必须参数")
    private Integer year;
    @ApiModelProperty(value = "季度")
    @NotNull(message = "季度为必须参数")
    private Integer quarter;

}
