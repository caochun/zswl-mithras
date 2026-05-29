package cn.zswltech.mithras.service.mapper.model.afterlease;

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
 * @date 2022/11/16
 * @description
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("new_after_lease_check_report_base")
public class NewAfterLeaseCheckReportBase extends BaseModel implements Serializable, IEntity {
    private static final long serialVersionUID = -5134864484507677663L;

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("check_plan_client_id")
    private Long checkPlanClientId;

    @TableField("check_period_start")
    private LocalDate checkPeriodStart;

    @TableField("check_period_end")
    private LocalDate checkPeriodEnd;

    @TableField("main_person")
    private String mainPerson;

    @TableField("job")
    private String job;

    @TableField("contact_way")
    private String contactWay;

    @TableField("contract_amount")
    private Long contractAmount;

    @TableField("risk_exposure")
    private Long riskExposure;

    @TableField("deadline")
    private LocalDate deadline;

    @TableField("client_name")
    private String clientName;

    @TableField("industry")
    private String industry;

    @TableField("next_repay_date")
    private LocalDate nextRepayDate;

    @TableField("next_repay_amount")
    private Long nextRepayAmount;

    @TableField("biz_dept_id")
    private Long bizDeptId;

    @TableField("biz_dept_name")
    private String bizDeptName;

//    @TableField("project_sponsor_id")
//    private Long projectSponsorId;
//
//    @TableField("project_sponsor_name")
//    private String projectSponsorName;

    @TableField("sponsor_user_id")
    private Long sponsorUserId;

    @TableField("sponsor_user_name")
    private String sponsorUserName;

    @TableField("risk_manager_id")
    private Long riskManagerId;

    @TableField("risk_manager_name")
    private String riskManagerName;

    @TableField("check_way")
    private String checkWay;

    @TableField("client_id")
    private Long clientId;

    /**
     * 剩余本金
     */
    @TableField("remaining_principal")
    private Long remainingPrincipal;

    @Override
    public void setMainId(Long id) {
        this.checkPlanClientId = id;
    }

    @Override
    public Long getMainId() {
        return this.checkPlanClientId;
    }
}
