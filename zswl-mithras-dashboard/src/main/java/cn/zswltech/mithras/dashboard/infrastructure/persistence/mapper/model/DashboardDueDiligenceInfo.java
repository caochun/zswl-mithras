package cn.zswltech.mithras.dashboard.infrastructure.persistence.mapper.model;

import cn.zswltech.mithras.service.mapper.model.BaseModelWithLogicDelete;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 工作台-尽调明细表
 * </p>
 *
 * @author chenyifei
 * @since 2024-07-29
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("dashboard_due_diligence_info")
public class DashboardDueDiligenceInfo extends BaseModelWithLogicDelete implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 部门id
     */
    @TableField("dept_id")
    private Long deptId;

    @TableField("dept_name")
    private String deptName;

    /**
     * 项目名称
     */
    @TableField("proj_name")
    private String projName;

    /**
     * 项目编号
     */
    @TableField("proj_code")
    private String projCode;

    /**
     * 评审id
     */
    @TableField("proj_review_id")
    private Long projReviewId;

    /**
     * 金额
     */
    @TableField("amount")
    private Long amount;

    /**
     * 项目主办
     */
    @TableField("proj_manager_id")
    private Long projManagerId;

    /**
     * 项目主办名称
     */
    @TableField("proj_manager_name")
    private String projManagerName;

    /**
     * 风控经理id
     */
    @TableField("risk_manager_id")
    private Long riskManagerId;

    /**
     * 风控经理名称
     */
    @TableField("risk_manager_name")
    private String riskManagerName;

    /**
     * 业务组分类
     */
    @TableField("business_group")
    private String businessGroup;

    /**
     * 尽调时间
     */
    @TableField("due_diligence_time")
    private LocalDateTime dueDiligenceTime;

    /**
     * 尽调报告出具时间
     */
    @TableField("due_diligence_report_time")
    private LocalDateTime dueDiligenceReportTime;

    /**
     * 备注
     */
    @TableField("comment")
    private String comment;


}
