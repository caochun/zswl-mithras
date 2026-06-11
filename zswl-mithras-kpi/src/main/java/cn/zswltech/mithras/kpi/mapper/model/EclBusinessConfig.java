package cn.zswltech.mithras.kpi.mapper.model;

import cn.zswltech.mithras.kpi.enums.config.EclConfigEnum;
import cn.zswltech.mithras.foundation.persistence.model.BaseModel;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @description ecl_业务配置表
 * @author vico
 * @date 2025-09-24
 */
@Data
public class EclBusinessConfig extends BaseModel implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
    * id
    */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
    * 配置模块
    */
    @TableField("config_module")
    private String configModule;

    /**
    * 配置code {@link EclConfigEnum#name()}
    */
    @TableField("config_code")
    private String configCode;

    /**
    * 配置名称
    */
    @TableField("config_name")
    private String configName;

    /**
    * 配置详情
    */
    @TableField("config_value")
    private String configValue;

    @TableField("config_enum")
    private String configEnum;

    @TableField("order_flag")
    private Integer orderFlag;

    /**
    * 版本时间
    */
    @TableField("version_time")
    private LocalDateTime versionTime;

    /**
    * 配置版本号
    */
    @TableField("config_version")
    private String configVersion;

}
