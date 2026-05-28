package cn.zswltech.mithras.dto.ftp;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2023/1/9 19:06
 */
@Data
@ApiModel("FTP多选-请求体")
public class FtpBatchIdsReq {
    @NotNull
    @ApiModelProperty("id")
    private List<Long> ids;
}
