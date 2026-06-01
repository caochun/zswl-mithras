package cn.zswltech.mithras.finance.view.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 工作台还本付息子列表信息表(DashboardFvRepayPrincipalInterestSub)表实体类
 *
 * @author makejava
 * @since 2025-09-02 09:30:35
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("dashboard_fv_repay_principal_interest_sub")
public class DashboardFvRepayPrincipalInterestSub extends Model<DashboardFvRepayPrincipalInterestSub> {
    /**
     * Id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 主表Id
     */
    @TableField("main_id")
    private Long mainId;

    /**
     * 关联项目名称
     */
    @TableField("related_proj_name")
    private String relatedProjName;

    /**
     * 关联合同编号
     */
    @TableField("related_contract_code")
    private String relatedContractCode;

    /**
     * 租金计划收款金额
     */
    @TableField("rent_plan_collection_amount")
    private BigDecimal rentPlanCollectionAmount;

    /**
     * 租金计划收款日期
     */
    @TableField("rent_plan_collection_date")
    private LocalDate rentPlanCollectionDate;

    /**
     * 银行账户类型编码
     */
    @TableField("bank_account_type_code")
    private String bankAccountTypeCode;

    /**
     * 银行账户类型显示
     */
    @TableField("bank_account_type_display")
    private String bankAccountTypeDisplay;

    /**
     * 创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;


    /**
     * 获取主键值
     *
     * @return 主键值
     */
    @Override
    protected Serializable pkVal() {
        return this.id;
    }
}

