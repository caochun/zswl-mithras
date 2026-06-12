package cn.zswltech.mithras.workflow.persistence.model;

import cn.zswltech.mithras.foundation.persistence.model.BaseModel;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author luyi
 */

/**
 * @author zhengkai.blog.csdn.net
 * @description flow_task_duration
 * @date 2023-09-26
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class FlowTaskDuration extends BaseModel implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;
    /**
     * 流程实例id
     */
    private String procInstId;

    /**
     * 任务id
     */
    private String taskId;

    /**
     * task定义key；e.g：userTask_bizDivisionLeader
     */
    private String taskDefKey;

    /**
     * 审批人id
     */
    private Long assignee;

    /**
     * 任务创建时间
     */
    private LocalDateTime taskStartAt;

    /**
     * 任务结束时间
     */
    private LocalDateTime taskEndAt;

    /**
     * 耗费工作日
     */
    private Integer taskCostWorkdays;

    /**
     * 任务耗费天数
     */
    private Integer taskCostDays;


}
