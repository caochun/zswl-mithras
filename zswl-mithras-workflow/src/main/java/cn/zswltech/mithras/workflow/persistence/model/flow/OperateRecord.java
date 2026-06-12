package cn.zswltech.mithras.workflow.persistence.model.flow;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @description 操作记录表
 * @author zhouning
 * @date 2024-06-19
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("operate_record")
public class OperateRecord implements Serializable {

    /**
    * id
    */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 操作类型
     */
    @TableField("type")
    private String type;

    /**
     * 操作-业务自扩展类型
     */
    @TableField("biz_type")
    private String bizType;

    /**
    * 流程id
    */
    @TableField("process_instance_id")
    private String processInstanceId;

    /**
     * 流程定义id
     */
    @TableField("process_definition_id")
    private String processDefinitionId;

    /**
     * 任务id，可以为空，比如操作流程撤回
     */
    @TableField("task_id")
    private String taskId;

    /**
     * 任务节点id，可以为空
     */
    @TableField("task_activity_id")
    private String taskActivityId;

    /**
     * 操作备注
     */
    @TableField("note")
    private String note;

    /**
     * 操作人
     */
    @TableField("handler_id")
    private String handlerId;

    /**
    * 流程发起时间
    */
    @TableField("gmt_create")
    private LocalDateTime gmtCreate;

    /**
    * 流程修改时间
    */
    @TableField("gmt_modify")
    private LocalDateTime gmtModify;

}
