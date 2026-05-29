package cn.zswltech.mithras.service.mapper.model;
import cn.zswltech.mithras.common.model.BaseModel;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;
import java.io.Serializable;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.NoArgsConstructor;

/**
 * @description 汉得流程操作记录表
 * @author yeqing
 * @date 2022-08-19
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HandProcessOperateRecord extends BaseModel {

    /**
    * id
    */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
    * 流程id
    */
    @TableField("process_instance_id")
    private String processInstanceId;

    /**
    * 流程发起时间
    */
    @TableField("start_time")
    private LocalDateTime startTime;

    /**
    * 流程结束时间
    */
    @TableField("end_time")
    private LocalDateTime endTime;

    /**
    * 流程实例名称
    */
    @TableField("process_instance_name")
    private String processInstanceName;

    /**
     * 发起人部门
     */
    @TableField("start_user_dept_id")
    private Long startUserDeptId;

    /**
     * 发起人id
     */
    @TableField("start_user_id")
    private Long startUserId;

    /**
    * 发起人名称
    */
    @TableField("start_user_name")
    private String startUserName;

    /**
    * 模型名称
    */
    @TableField("model_name")
    private String modelName;

    /**
    * 操作详情数组
    */
    @TableField("operate_info")
    private String operateInfo;

}