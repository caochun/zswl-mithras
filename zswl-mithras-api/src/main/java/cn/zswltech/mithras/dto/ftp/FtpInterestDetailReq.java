package cn.zswltech.mithras.dto.ftp;

import cn.zswltech.mithras.dto.PageReq;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

/**
 * @author dingqi
 * @date 2023/5/17
 * @description
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel("FTP计息-每日计息列表-请求参数")
public class FtpInterestDetailReq extends PageReq {
    @ApiModelProperty("FTP计息记录id")
    @NotNull(message = "FTP计息id不能为空")
    private Long ftpInterestId;

    @ApiModelProperty("FTP计息日期-起 yyyy-MM-dd")
    private String interestDateFrom;

    @ApiModelProperty("FTP计息日期-止 yyyy-MM-dd")
    private String interestDateTo;
}
