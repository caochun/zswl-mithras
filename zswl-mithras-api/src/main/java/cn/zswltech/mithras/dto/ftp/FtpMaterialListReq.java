package cn.zswltech.mithras.dto.ftp;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2023/1/10 19:50
 */
@Data
@ApiModel("ftp资料列表-请求体")
public class FtpMaterialListReq extends FtpGuidanceIdReq{
    @NotNull(message = "bizType必填")
    @ApiModelProperty("FTP_QUARTERLY_GUIDANCE(季度)、FTP_MONTHLY_GUIDANCE(月度)")
    private String bizType;

    @ApiModelProperty("MEETING_FILE(会议纪要), SUPPLEMENT(补充资料)")
    private String type;
}
