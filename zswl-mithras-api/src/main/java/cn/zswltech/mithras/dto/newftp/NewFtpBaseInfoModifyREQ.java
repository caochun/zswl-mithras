package cn.zswltech.mithras.dto.newftp;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;
/**
 * @description ftp主表
 * @author zhaozhengkang
 * @date 2023-05-21
 */
@Data
@ApiModel("ftp主表编辑-请求体")
public class NewFtpBaseInfoModifyREQ {

    /**
    * id
    */
    @ApiModelProperty(value = "id")
    private Long id;

    /**
    * 所属月份
    */
    @ApiModelProperty(value = "所属月份")
    private LocalDateTime month;

    /**
    * ftp月度指导id
    */
    @ApiModelProperty(value = "ftp月度指导id")
    private Long monthlyGuidanceId;

    /**
    * 季度指导id，1，4，7，10月份不为null
    */
    @ApiModelProperty(value = "季度指导id，1，4，7，10月份不为null")
    private Long quarterlyGuidanceId;

    /**
    * 最新版本号
    */
    @ApiModelProperty(value = "最新版本号")
    private String newestVersion;

}
