package cn.zswltech.mithras.contract.overdue.mapper.model;

import cn.zswltech.mithras.foundation.persistence.model.BaseModel;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

/**
 * @description: 逾期催收
 * @author: zhaozhengkang
 * @date: 2024/10/21 16:20
 */
@Data
@TableName("oc_overdue_collection")
public class OverdueCollection extends BaseModel {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("client_id")
    private Long clientId;

    @TableField("client_name")
    private String clientName;

    @TableField("risk_exposure")
    private Long riskExposure;

    @TableField(value = "overdue_rent", updateStrategy = FieldStrategy.IGNORED)
    private Long overdueRent;
    /**
     * 逾期罚息
     */
    @TableField(value = "late_charge", updateStrategy = FieldStrategy.IGNORED)
    private Long lateCharge;

    @TableField(value = "cur_max_overdue_days", updateStrategy = FieldStrategy.IGNORED)
    private Integer curMaxOverdueDays;

    @TableField("project_sponsor")
    private Long projectSponsor;

    @TableField("project_sponsor_name")
    private String projectSponsorName;

    @TableField("biz_dept")
    private Long bizDept;

    @TableField("biz_dept_name")
    private String bizDeptName;

    @TableField("overdue")
    private Boolean overdue;

    @TableField("lock_version")
    private Long lockVersion;
}
