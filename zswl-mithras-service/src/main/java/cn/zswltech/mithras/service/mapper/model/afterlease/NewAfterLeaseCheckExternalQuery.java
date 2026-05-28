package cn.zswltech.mithras.service.mapper.model.afterlease;

import cn.zswltech.mithras.service.mapper.model.BaseModel;
import cn.zswltech.mithras.service.mapper.model.SponsorField;
import cn.zswltech.mithras.service.mapper.tag.IEntity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @author zhaozhengkang
 * @description 租后检查外部查询任务
 * @date 2022-11-17
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("new_after_lease_check_external_query")
@SponsorField(value = "sponsorUserId", belongDeptField = "deptId", cosponsorField = "")
public class NewAfterLeaseCheckExternalQuery extends BaseModel implements Serializable, IEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("inspection_month")
    private LocalDateTime inspectionMonth;

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
     * 主办id
     */
    @TableField("sponsor_user_id")
    private Long sponsorUserId;

    /**
     * 检查日期
     */
    @TableField("inspection_date")
    private LocalDate inspectionDate;

    /**
     * 合同最终到期日
     */
    @TableField("deadline")
    private LocalDate deadline;

    /**
     * 审批通过时间
     */
    @TableField("approval_pass_time")
    private LocalDateTime approvalPassTime;

    /**
     * 状态
     */
    @TableField("approval_status")
    private String approvalStatus;

    /**
     * 租后检查部门
     */
    @TableField("dept_id")
    private Long deptId;

    /**
     * 行业
     */
    @TableField("industry_type")
    private String industryType;

    /**
     * 风险敞口余额
     */
    @TableField("risk_exposure")
    private Long riskExposure;

    /**
     * 合同金额
     */
    @TableField("contract_total_amount")
    private Long contractTotalAmount;

    /**
     * 下次还款日
     */
    @TableField("next_repay_date")
    private LocalDate nextRepayDate;

    /**
     * 下次还款金额
     */
    @TableField("next_repay_amount")
    private Long nextRepayAmount;

    /**
     * 风险信号及重大事项、风险防范措施
     */
    @TableField("preventive_measures")
    private String preventiveMeasures;

    /**
     * 查询分析与查询结论
     */
    @TableField("query_conclusion")
    private String queryConclusion;

    @TableField("biz_dept_leader")
    private Long bizDeptLeader;

    @TableField("biz_division_leader")
    private Long bizDivisionLeader;

    @TableField(value = "process_instance_id", updateStrategy = FieldStrategy.IGNORED)
    private String processInstanceId;

    @Override
    public void setMainId(Long id) {
        this.id = id;
    }

    @Override
    public Long getMainId() {
        return this.id;
    }
}
