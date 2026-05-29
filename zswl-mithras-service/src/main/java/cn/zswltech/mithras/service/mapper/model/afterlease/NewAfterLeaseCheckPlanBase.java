package cn.zswltech.mithras.service.mapper.model.afterlease;

import cn.zswltech.mithras.service.enums.afterlease.*;
import cn.zswltech.mithras.common.model.BaseModel;
import cn.zswltech.mithras.common.model.IEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * @author dingqi
 * @date 2022/11/8
 * @description 租后检查计划
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("new_after_lease_check_plan_base")
public class NewAfterLeaseCheckPlanBase extends BaseModel implements Serializable, IEntity {
    private static final long serialVersionUID = -3043356925916020255L;

    /**
     * 主键id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 计划名称
     */
    @TableField("plan_name")
    private String planName;

    /**
     * 计划类型 {@link AfterLeaseCheckPlanTypeEnum#name()}
     */
    @TableField("plan_type")
    private String planType;

    /**
     * 检查所属年份
     */
    @TableField("year")
    private Integer year;

    /**
     * 检查所属季度
     */
    @TableField("quarter")
    private Integer quarter;

    /**
     * 检查所属月度
     */
    @TableField("month")
    private Integer month;

    /**
     * 检查填报开始时间
     */
    @TableField("start_date")
    private LocalDate startDate;

    /**
     * 检查填报结束时间
     */
    @TableField("end_date")
    private LocalDate endDate;

    /**
     * 管理形式 {@link AfterLeaseCheckWayEnum#name()}
     */
    @TableField(value = "check_way")
    private String checkWay;

    /**
     * 上次管理形式 {@link AfterLeaseCheckWayEnum#name()}
     */
    @TableField(value = "last_check_way")
    private String lastCheckWay;

    @TableField(value = "last_end_date")
    private LocalDate lastEndDate;

    /**
     * 计划状态 {@link AfterLeaseCheckPlanStatusEnum#name()}
     */
    @TableField("plan_status")
    private String planStatus;

    /**
     * 审批状态 {@link AfterLeaseCheckPlanProcessStatusEnum#name()}
     */
    @TableField("approval_status")
    private String approvalStatus;

    /**
     * 截止时间
     */
    @TableField("dead_line")
    private LocalDate deadLine;

    /**
     * 截止时间标签 {@link AfterLeaseDeadlineLabelEnum#name()}
     */
    @TableField("deadline_label")
    private String deadlineLabel;

    //跟进频率
    /**
     * 跟进频率 {@link AfterLeaseCheckTermEnum#name()}
     */
    @TableField("term")
    private Integer term;

    @Override
    public void setMainId(Long id) {
        this.id = id;
    }

    @Override
    public Long getMainId() {
        return this.id;
    }
}
