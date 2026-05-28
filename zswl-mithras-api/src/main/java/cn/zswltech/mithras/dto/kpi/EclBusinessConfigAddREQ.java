package cn.zswltech.mithras.dto.kpi;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @description ecl_业务配置表
 * @author vico
 * @date 2025-09-24
 */
@Data
@ApiModel("ecl_业务配置表新增-请求体")
public class EclBusinessConfigAddREQ {

    /**
    * 配置模块
    */
    @ApiModelProperty(value = "配置模块")
    private String configModule;

    /**
    * 配置code
    */
    @ApiModelProperty(value = "配置code")
    private String configCode;

    /**
    * 配置名称
    */
    @ApiModelProperty(value = "配置名称")
    private String configName;

    /**
    * 配置详情
    */
    @ApiModelProperty(value = "配置详情")
    private String configValue;

    /**
    * 版本时间
    */
    @ApiModelProperty(value = "版本时间")
    private LocalDateTime versionTime;

    /**
    * 配置版本号
    */
    @ApiModelProperty(value = "配置版本号")
    private String configVersion;

}
