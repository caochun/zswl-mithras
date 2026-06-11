package cn.zswltech.mithras.budget.mapper.model;
import cn.zswltech.mithras.foundation.persistence.model.BaseModel;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * @description 预算管理-预算计划
 * @author vico
 * @date 2025-04-11
 */
@Data
public class BudgetPlan extends BaseModel implements Serializable {

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
    * 计划名称
    */
    @TableField("plan_name")
    private String planName;

    /**
    * 计划年份
    */
    @TableField("plan_year")
    private Integer planYear;

    /**
    * 计划月份
    */
    @TableField("plan_month")
    private Integer planMonth;

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
     * 是否收集
     */
    @TableField("need_collect")
    private Integer needCollect;

    /**
    * 收集截止日期
    */
    @TableField("collect_date_to")
    private LocalDate collectDateTo;

    /**
    * 是否有月度调整计划
    */
    @TableField("is_adjust")
    private Integer isAdjust;

    /**
    * 月度调整计划id
    */
    @TableField("adjust_plan_id")
    private Long adjustPlanId;

}
