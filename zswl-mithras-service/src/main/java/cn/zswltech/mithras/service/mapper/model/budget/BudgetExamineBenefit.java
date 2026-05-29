package cn.zswltech.mithras.service.mapper.model.budget;
import cn.zswltech.mithras.service.enums.BudgetExamineBenefitEnum;
import cn.zswltech.mithras.common.model.BaseModelWithLogicDelete;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;

/**
 * @description 预算管理-预算考核-效益考核表
 * @author vico
 * @date 2025-04-11
 */
@Data
public class BudgetExamineBenefit extends BaseModelWithLogicDelete implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
    * 主键id
    */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
    * 预算考核id
    */
    @TableField("budget_examine_id")
    private Long budgetExamineId;

    /**
    * 预算考核年份
    */
    @TableField("budget_examine_year")
    private Integer budgetExamineYear;

    /**
    * 预算考核月份
    */
    @TableField("budget_examine_month")
    private Integer budgetExamineMonth;

//    /**
//    * 合同id
//    */
//    @TableField("contract_id")
//    private Long contractId;

    /**
    * 业务部门id
    */
    @TableField("belong_dept_id")
    private Long belongDeptId;

    /**
    * 字段名称 {@link BudgetExamineBenefitEnum#name()}
    */
    @TableField("field_name")
    private String fieldName;

    /**
    * 字段值
    */
    @TableField("field_value")
    private Long fieldValue;

    /**
    * 字段层级
    */
    @TableField("field_level")
    private Integer fieldLevel;

}
