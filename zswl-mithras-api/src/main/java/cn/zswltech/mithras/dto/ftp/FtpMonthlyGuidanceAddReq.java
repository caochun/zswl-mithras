package cn.zswltech.mithras.dto.ftp;
import lombok.Data;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotNull;

/**
 * @description 月度指导
 * @author zhaozhengkang
 * @date 2023-01-10
 */
@Data
@ApiModel("月度指导新增-请求体")
public class FtpMonthlyGuidanceAddReq {

    @ApiModelProperty(value = "年度")
    @NotNull
    private Integer year;

    /**
    * 月度
    */
    @ApiModelProperty(value = "月度")
    @NotNull
    private Integer month;
}
