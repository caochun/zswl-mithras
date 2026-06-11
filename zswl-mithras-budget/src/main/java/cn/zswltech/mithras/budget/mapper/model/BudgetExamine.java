package cn.zswltech.mithras.budget.mapper.model;

import cn.zswltech.mithras.workflow.flow.enums.ProcessState;
import cn.zswltech.mithras.foundation.persistence.model.BaseModel;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * @description 预算管理-预算考核
 * @author vico
 * @date 2025-04-11
 */
@Data
public class BudgetExamine extends BaseModel implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
    * 主键id
    */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
    * 考核名称
    */
    @TableField("examine_name")
    private String examineName;

    /**
    * 考核年份
    */
    @TableField("examine_year")
    private Integer examineYear;

    /**
    * 考核月份
    */
    @TableField("examine_month")
    private Integer examineMonth;

    /**
    * 审批状态  {@link ProcessState#name()}
    */
    @TableField("approval_status")
    private String approvalStatus;

    /**
    * 提交人id
    */
    @TableField("submit_user_id")
    private Long submitUserId;

    /**
    * 提交时间
    */
    @TableField("submit_time")
    private LocalDate submitTime;

}
