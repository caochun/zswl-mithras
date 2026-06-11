package cn.zswltech.mithras.dashboard.mapper.model;

import cn.zswltech.mithras.foundation.persistence.model.BaseModelWithLogicDelete;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 合同投放计划表
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value ="contract_org_plan")
public class ContractOrgPlan extends BaseModelWithLogicDelete implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 年份
     */
    @TableField(value = "year")
    private int year;

    /**
     * 月份
     */
    @TableField(value = "month")
    private int month;

    /**
     * 部门id
     */
    @TableField(value = "dept_id")
    private Long deptId;


    /**
     * 部门code
     */
    @TableField(value = "dept_name")
    private String deptName;

    /**
     * 计划投放金额
     */
    @TableField(value = "plan_amount")
    private Long planAmount;


}