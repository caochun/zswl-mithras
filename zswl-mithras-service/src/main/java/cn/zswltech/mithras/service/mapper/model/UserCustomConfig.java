package cn.zswltech.mithras.service.mapper.model;

import cn.zswltech.mithras.service.plugin.IncludeNull;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author dingqi
 * @date 2025/2/11
 * @description
 */
@EqualsAndHashCode(callSuper = true)
@TableName("user_custom_config")
@Data
public class UserCustomConfig extends BaseModelWithLogicDelete {
    @TableId(type = IdType.AUTO)
    @TableField(value = "id")
    private Long id;

    @TableField(value = "user_id")
    private Long userId;

    @TableField(value = "config_key")
    private String configKey;

    @TableField(value = "config_value")
    private String configValue;

    @IncludeNull
    @TableField(value = "metadata_type")
    private String metadataType;
}
