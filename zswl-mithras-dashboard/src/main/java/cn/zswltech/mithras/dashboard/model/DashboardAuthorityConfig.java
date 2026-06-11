package cn.zswltech.mithras.dashboard.model;

import cn.zswltech.mithras.foundation.persistence.model.BaseModelWithLogicDelete;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 用户可查看工作台看板配置表
 * @author yangxiong
 * @TableName dashboard_authority_config
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value ="dashboard_authority_config")
public class DashboardAuthorityConfig extends BaseModelWithLogicDelete implements Serializable {
    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户id
     */
    @TableField(value = "user_id")
    private Long userId;

    /**
     * 看板key
     */
    @TableField(value = "dashboard_key")
    private String dashboardKey;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}