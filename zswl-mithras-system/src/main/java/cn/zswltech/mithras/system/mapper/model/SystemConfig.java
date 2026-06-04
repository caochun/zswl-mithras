package cn.zswltech.mithras.system.mapper.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author yibin
 */
@Accessors(chain = true)
@Data
@TableName("bifrost_system_config")
public class SystemConfig {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String configKey;

    private String configValue;

    private String description;

    /**
     * '状态，0：无效；1：生效'
     */
    private Integer status;

    /**
     * String、Boolean、Json'
     */
    private String type;


}
