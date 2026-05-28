package cn.zswltech.mithras.dto.newftp;

import cn.zswltech.mithras.dto.VersionBaseREQ;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author zhaozhengkang
 * @description ftp报价表
 * @date 2023-05-21
 */
@Data
@ApiModel("ftp报价表列表-请求体")
public class NewFtpDetailReq extends VersionBaseREQ {

    @ApiModelProperty(value = "主表id")
    private Long mainId;

}
