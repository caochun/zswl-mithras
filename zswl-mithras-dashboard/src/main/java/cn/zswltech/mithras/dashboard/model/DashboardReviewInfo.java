package cn.zswltech.mithras.dashboard.model;

import cn.zswltech.mithras.foundation.persistence.model.BaseModelWithLogicDelete;
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
 * 工作台-评审明细表
 * </p>
 *
 * @author chenyifei
 * @since 2024-07-29
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("dashboard_review_info")
public class DashboardReviewInfo extends BaseModelWithLogicDelete implements Serializable {

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
     * 项目金额
     */
    @TableField("proj_amount")
    private Long projAmount;

    /**
     * 批复金额
     */
    @TableField("approval_amount")
    private Long approvalAmount;

    /**
     * 业务组分类
     */
    @TableField("business_group")
    private String businessGroup;

    /**
     * 召开时间
     */
    @TableField("convoke_time")
    private LocalDateTime convokeTime;

    /**
     * 评审结果
     */
    @TableField("review_result")
    private String reviewResult;


}
