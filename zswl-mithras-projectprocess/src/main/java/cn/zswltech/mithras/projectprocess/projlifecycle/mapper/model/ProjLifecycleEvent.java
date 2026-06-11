package cn.zswltech.mithras.projectprocess.projlifecycle.mapper.model;

import cn.zswltech.mithras.foundation.persistence.model.BaseModel;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @create: 2022-10-26
 **/
@Data
public class ProjLifecycleEvent extends BaseModel implements Serializable{
    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 项目id
     */
    @TableField("proj_id")
    private Long projId;

    /**
     * 事件时间
     */
    @TableField("event_time")
    private LocalDateTime eventTime;

    /**
     * 事件类型
     */
    @TableField("event_type")
    private String eventType;

    /**
     * 事件
     */
    @TableField("event")
    private String event;

    /**
     * 操作人
     */
    @TableField("operator")
    private Long operator;

    /**
     * 事件描述
     */
    @TableField("event_desc")
    private String eventdesc;

    /**
     * 项目类型
     */
    @TableField("proj_type")
    private String projType;

}
