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
 * 工作台授信信息快照表(DashboardFvCreditInfoSnapshot)表实体类
 *
 * @author makejava
 * @since 2025-09-02 09:30:34
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("dashboard_fv_credit_info_snapshot")
public class DashboardFvCreditInfoSnapshot extends Model<DashboardFvCreditInfoSnapshot> {
    /**
     * Id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 卡片id
     */
    @TableField("card_id")
    private Long cardId;

    /**
     * 授信编号
     */
    @TableField("credit_code")
    private String creditCode;

    /**
     * 机构名称
     */
    @TableField("org_name")
    private String orgName;

    /**
     * 产品类型code
     */
    @TableField("financing_biz_type_code")
    private String financingBizTypeCode;

    /**
     * 产品类型display
     */
    @TableField("financing_biz_type_display")
    private String financingBizTypeDisplay;

    /**
     * 总授信额度
     */
    @TableField("credit_total_amount")
    private BigDecimal creditTotalAmount;

    /**
     * 已使用授信额度
     */
    @TableField("credit_used_amount")
    private BigDecimal creditUsedAmount;

    /**
     * 是否可循环，1-是，0-否
     */
    @TableField("is_cycle")
    private Integer isCycle;

    /**
     * 授信到期日
     */
    @TableField("deadline")
    private LocalDate deadline;

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

