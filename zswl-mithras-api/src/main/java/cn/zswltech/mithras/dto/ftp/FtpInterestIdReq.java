package cn.zswltech.mithras.dto.ftp;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author dingqi
 * @date 2023/5/17
 * @description
 */
@Data
public class FtpInterestIdReq {
    @ApiModelProperty("FTP计息记录id")
    @NotNull(message = "FTP计息id不能为空")
    private Long ftpInterestId;
}
