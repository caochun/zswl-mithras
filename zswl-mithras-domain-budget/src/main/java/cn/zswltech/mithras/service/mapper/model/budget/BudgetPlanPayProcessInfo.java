package cn.zswltech.mithras.service.mapper.model.budget;

import cn.zswltech.mithras.service.mapper.model.BaseModelWithLogicDelete;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author dingqi
 * @date 2025/6/13
 * @description
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("budget_plan_pay_process_info")
public class BudgetPlanPayProcessInfo extends BaseModelWithLogicDelete {
    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    @TableField(value = "id")
    private Long id;

    /**
     * 预算ID
     */
    @TableField(value = "budget_plan_id")
    private Long budgetPlanId;

    /**
     * 投放计划ID
     */
    @TableField(value = "budget_plan_pay_id")
    private Long budgetPlanPayId;

    /**
     * 部门ID
     */
    @TableField(value = "belong_dept_id")
    private Long belongDeptId;

    /**
     * 收集流程实例ID
     */
    @TableField(value = "process_instance_id")
    private String processInstanceId;

    /**
     * 是否收集完毕
     */
    @TableField(value = "is_collect_finish")
    private Integer isCollectFinish;

    /**
     * 流程是否通过
     */
    @TableField(value = "is_process_pass")
    private Integer isProcessPass;
}
