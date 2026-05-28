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
@ApiModel("ecl_业务配置表详情-返回体")
public class EclBusinessConfigDetailRSP {

    /**
    * id
    */
    @ApiModelProperty(value = "id")
    private Long id;

    /**
    * 配置模块
    */
    @ApiModelProperty(value = "配置模块")
    private String configModule;

    /**
    * 配置code
    */
    @ApiModelProperty(value = "配置code EclConfigEnum")
    private String configCode;

    /**
    * 配置名称
    */
    @ApiModelProperty(value = "配置名称 EclConfigEnum")
    private String configName;

    /**
    * 配置详情
    */
    @ApiModelProperty(value = "配置详情")
    private String configValue;

    /**
     * 配置枚举
     */
    @ApiModelProperty(value = "配置枚举")
    private String configEnum;

    /**
    * 版本时间
    */
    @ApiModelProperty(value = "版本时间")
    private LocalDateTime versionTime;

}
