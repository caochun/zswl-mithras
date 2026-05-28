package cn.zswltech.mithras.dto.newftp;

import cn.zswltech.mithras.dto.VersionBaseREQ;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author zhaozhengkang
 * @description ftp报价表
 * @date 2023-05-21
 */
@Data
@ApiModel("ftp报价表列表-请求体")
public class NewFtpCommonDetailReq extends VersionBaseREQ {

    @ApiModelProperty(value = "主表id")
    @NotNull(message = "主表id不能为空")
    private Long mainId;

    @ApiModelProperty("频率")
    private String frequency;

    @ApiModelProperty("分页，默认1")
    private Integer page = 1;

    @ApiModelProperty("页大小， 默认20")
    private Integer pageSize = 20;

}
