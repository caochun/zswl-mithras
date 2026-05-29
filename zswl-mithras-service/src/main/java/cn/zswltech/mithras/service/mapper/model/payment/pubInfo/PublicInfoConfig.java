package cn.zswltech.mithras.service.mapper.model.payment.pubInfo;

import cn.zswltech.mithras.common.model.BaseModelWithLogicDelete;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 公开信息配置表
 *
 * @author bigbear
 * @TableName public_info_config
 */
@Data
@TableName(value = "public_info_config")
@EqualsAndHashCode(callSuper = true)
public class PublicInfoConfig extends BaseModelWithLogicDelete implements Serializable {
    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 配置标识
     */
    @TableField(value = "config_key")
    private String configKey;

    /**
     * 排序优先级
     */
    @TableField(value = "sort_priority")
    private Integer sortPriority;

    /**
     * 标题
     */
    @TableField(value = "title")
    private String title;

    /**
     * 描述信息
     */
    @TableField(value = "description")
    private String description;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}