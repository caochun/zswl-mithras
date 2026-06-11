package cn.zswltech.mithras.afterlease.mapper.model;

import cn.zswltech.mithras.afterlease.enums.AfterLeaseCheckWayEnum;
import cn.zswltech.mithras.afterlease.enums.AfterLeaseDeadlineLabelEnum;
import cn.zswltech.mithras.foundation.persistence.model.BaseModel;
import cn.zswltech.mithras.foundation.persistence.model.SponsorField;
import cn.zswltech.mithras.foundation.persistence.tag.IEntity;
import cn.zswltech.mithras.foundation.persistence.plugin.IncludeNull;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @author dingqi
 * @date 2022/11/8
 * @description 租后检查计划需检查客户
 */
@SponsorField(value = "belongSponsorId", belongDeptField = "belongDeptId")
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("new_after_lease_check_plan_client")
public class NewAfterLeaseCheckPlanClient extends BaseModel implements Serializable, IEntity {
    private static final long serialVersionUID = -7774217764791266961L;

    /**
     * 主键id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 租后检查计划id
     */
    @TableField("plan_id")
    private Long planId;

    /**
     * 客户id
     */
    @TableField("client_id")
    private Long clientId;

    /**
     * 客户名称
     */
    @TableField("client_name")
    private String clientName;

    /**
     * 部门id
     */
    @TableField("belong_dept_id")
    private Long belongDeptId;

    /**
     * 主办id
     */
    @TableField("belong_sponsor_id")
    private Long belongSponsorId;

    /**
     * 是否需要检查
     */
    @TableField("is_check")
    private Integer isCheck;

    /**
     * 检查形式 {@link AfterLeaseCheckWayEnum#name()}
     */
    @TableField(value = "check_way")
    @IncludeNull
    private String checkWay;

    /**
     * 协查风控经理id
     */
    @TableField(value = "risk_manager_id")
    @IncludeNull
    private Long riskManagerId;

    /**
     * 协查风控经理名称
     */
    @TableField(value = "risk_manager_name")
    @IncludeNull
    private String riskManagerName;

    @TableField("stock_risk_exposure")
    private Long stockRiskExposure;

    @TableField(value = "check_time")
    @IncludeNull
    private LocalDate checkTime;

    @TableField("check_fill_time")
    private LocalDate checkFillTime;

    @TableField(value = "is_notify")
    private Boolean isNotify;

    /**
     * 投放日期
     */
    @TableField("payment_date")
    private LocalDate paymentDate;

    /**
     * 审批状态
     */
    @TableField("approval_status")
    private String approvalStatus;

    /**
     * 截止时间
     */
    @TableField("next_deadline")
    private LocalDate nextDeadline;

    /**
     * 临时截止时间 (流程审批通过后需要拷贝到deadLine)
     */
    @TableField("tmp_next_deadline")
    private LocalDate tmpNextDeadline;

    /**
     * 剩余本金
     */
    @TableField("remaining_principal")
    private Long remainingPrincipal;

    /**
     * 下次租后检查形式
     */
    @TableField("next_check_way")
    private String nextCheckWay;

    /**
     * 临时-下次租后检查形式
     */
    @TableField("tmp_next_check_way")
    private String tmpNextCheckWay;

    /**
     * 担保人ids
     */
    @TableField("guarantee_ids")
    private String guaranteeIds;

    /**
     * 担保人名称集合
     */
    @TableField("guarantee_names")
    private String guaranteeNames;

    /**
     * 是否是首次检查的标识
     */
    @TableField("first_check_flag")
    private Integer firstCheckFlag;

    /**
     * 是否已发起下次检查
     */
    @TableField("next_check_flag")
    private Integer nextCheckFlag;

    /**
     * 逾期天数
     */
    @TableField("overdue_days")
    private Integer overdueDays;

    /**
     * 提交审批时间
     */
    @TableField("commit_time")
    private LocalDateTime commitTime;

    @Override
    public void setMainId(Long id) {
        this.planId = id;
    }

    @Override
    public Long getMainId() {
        return this.planId;
    }
}
