package cn.zswltech.mithras.service.mapper.model.budget;
import cn.zswltech.mithras.common.model.BaseModel;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * @description 预算管理-预算计划-投放计划
 * @author vico
 * @date 2025-04-11
 */
@Data
public class BudgetPlanPay extends BaseModel implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
    * 主键id
    */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
    * 逻辑删除，0-未删除
    */
    @TableField("deleted")
    private Integer deleted;

    /**
    * 预算计划id
    */
    @TableField("budget_plan_id")
    private Long budgetPlanId;

    /**
    * 计划名称
    */
    @TableField("budget_plan_name")
    private String budgetPlanName;

    /**
    * 预算计划开始日期
    */
    @TableField("budget_date_from")
    private LocalDate budgetDateFrom;

    /**
    * 预算计划结束日期
    */
    @TableField("budget_date_to")
    private LocalDate budgetDateTo;

    /**
    * 计划填报开始日期
    */
    @TableField("write_date_from")
    private LocalDate writeDateFrom;

    /**
    * 计划填报结束日期
    */
    @TableField("write_date_to")
    private LocalDate writeDateTo;

    /**
    * 预算类型
    */
    @TableField("budget_type")
    private String budgetType;

    /**
    * 状态
    */
    @TableField("budget_status")
    private String budgetStatus;

    /**
    * 收集截止日期
    */
    @TableField("collect_date_to")
    private LocalDate collectDateTo;

}
