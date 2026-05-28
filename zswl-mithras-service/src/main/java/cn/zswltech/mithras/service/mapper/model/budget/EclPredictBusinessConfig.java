package cn.zswltech.mithras.service.mapper.model.budget;
import cn.zswltech.mithras.service.mapper.model.BaseModelWithLogicDelete;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @description ecl_预测业务配置表
 * @author vico
 * @date 2025-10-14
 */
@Data
public class EclPredictBusinessConfig extends BaseModelWithLogicDelete implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
    * id
    */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
    * 预测计划id
    */
    @TableField("execute_predict_id")
    private Long executePredictId;

    /**
    * 配置模块
    */
    @TableField("config_module")
    private String configModule;

    /**
    * 配置code
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

    /**
    * 排序
    */
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

    @TableField("config_enum")
    private String configEnum;


}
