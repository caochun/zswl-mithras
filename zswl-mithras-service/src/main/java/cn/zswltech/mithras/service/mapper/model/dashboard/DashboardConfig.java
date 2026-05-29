package cn.zswltech.mithras.service.mapper.model.dashboard;

import cn.zswltech.mithras.common.model.BaseModelWithLogicDelete;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 工作台看板配置表
 * @author yangxiong
 * @TableName dashboard_config
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value ="dashboard_config")
public class DashboardConfig extends BaseModelWithLogicDelete implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 看板key
     */
    @TableField(value = "dashboard_key")
    private String dashboardKey;

    /**
     * 看板名称
     */
    @TableField(value = "dashboard_display")
    private String dashboardDisplay;

    /**
     * 排序权重
     */
    @TableField(value = "order_num")
    private Integer orderNum;
}