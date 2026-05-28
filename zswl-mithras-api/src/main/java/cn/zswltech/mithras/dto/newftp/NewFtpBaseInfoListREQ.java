package cn.zswltech.mithras.dto.newftp;

import cn.zswltech.mithras.dto.PageReq;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;

/**
 * @author zhaozhengkang
 * @description ftp主表
 * @date 2023-05-21
 */
@Data
@ApiModel("ftp主表列表-请求体")
public class NewFtpBaseInfoListREQ extends PageReq {

    @ApiModelProperty(value = "所属月份")
    private LocalDate month;
    @ApiModelProperty(value = "创建人")
    private Long createBy;
    @ApiModelProperty(value = "流程状态")
    private String ftpProcessStatus;
    @ApiModelProperty(value = "创建开始时间")
    private LocalDate createTimeFrom;
    @ApiModelProperty(value = "创建结束时间")
    private LocalDate createTimeTo;
    @ApiModelProperty(value = "生效开始时间")
    private LocalDate effectTimeFrom;
    @ApiModelProperty(value = "生效结束时间")
    private LocalDate effectTimeTo;

}
